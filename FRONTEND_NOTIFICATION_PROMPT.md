# Frontend AI Prompt — Notification & Messaging System (JobStream)

Use this prompt verbatim when talking to an AI assistant (ChatGPT, Claude, Copilot, etc.)
to implement the notification/messaging feature in the frontend.

---

## PROMPT

You are implementing the **real-time notification and messaging system** for a LinkedIn-like
job platform called **JobStream**. The backend is a Spring Boot application using STOMP over
SockJS for WebSocket communication. Below is the **complete, authoritative specification**
of the backend behavior. Implement the frontend exactly as described.

---

### 1. Tech stack / context

- Frontend can be React, Vue, or Angular — adapt the code to whatever framework is in use.
- HTTP auth: every REST call needs the header `Authorization: Bearer <accessToken>`.
- WebSocket auth: the JWT access token is sent as a STOMP header at connect time
  (see Section 3).
- Base URL for REST: `http://<host>/api`
- WebSocket endpoint: `http://<host>/ws` (SockJS)

---

### 2. Notification types

The backend has exactly these notification types (string enum):

```
MESSAGE             — a new chat message from another user
CONNECTION_REQUEST  — someone sent a connection request
CONNECTION_ACCEPTED — your connection request was accepted
JOB_APPLICATION     — someone applied to your job posting
APPLICATION_ACCEPTED — your job application was accepted
APPLICATION_REJECTED — your job application was rejected
```

**Rule for UI badges:**
- **Message button badge** → count notifications whose `type === "MESSAGE"` and `isRead === false`
- **Notification bell badge** → count notifications whose `type !== "MESSAGE"` and `isRead === false`

---

### 3. WebSocket connection (STOMP over SockJS)

```js
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

let stompClient = null;

function connectWebSocket(accessToken, onNotification, onCountUpdate) {
  stompClient = new Client({
    webSocketFactory: () => new SockJS('http://<host>/ws'),

    // --- CRITICAL: send JWT in STOMP CONNECT frame ---
    connectHeaders: {
      Authorization: `Bearer ${accessToken}`,
    },

    // Heartbeat: must match the server's 10 000 ms setting.
    // If either side is 0, heartbeats are disabled.
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,

    reconnectDelay: 5000,   // auto-reconnect after 5 s on disconnect

    onConnect: () => {
      // 1. Subscribe: receive new individual notifications
      //    ⚠️  Do NOT put the email in the path here.
      //    Spring transforms "/user/queue/..." subscriptions using the session's
      //    Principal — adding the email breaks the matching and you get nothing.
      stompClient.subscribe(
        `/user/queue/notifications`,
        (frame) => {
          const notification = JSON.parse(frame.body);
          onNotification(notification);
        }
      );

      // 2. Subscribe: receive updated split badge counts
      // Payload shape: { notificationCount: number, messageCount: number }
      stompClient.subscribe(
        `/user/queue/notifications/count`,
        (frame) => {
          const counts = JSON.parse(frame.body);
          onCountUpdate(counts); // { notificationCount, messageCount }
        }
      );
    },

    onStompError: (frame) => {
      console.error('STOMP error', frame.headers['message'], frame.body);
    },

    onDisconnect: () => {
      console.log('WS disconnected');
    },

    onWebSocketClose: (event) => {
      // The client auto-reconnects after reconnectDelay ms.
      // Do NOT create a new Client — the same client will retry.
      console.log('WS closed, will reconnect', event.code);
    },
  });

  stompClient.activate();
}

function disconnectWebSocket() {
  if (stompClient) stompClient.deactivate();
}
```

> **Session-closed prevention:** Use `heartbeatIncoming: 10000, heartbeatOutgoing: 10000`
> (same as the server). Never set them to `0` unless you also disable them server-side.
> The `reconnectDelay: 5000` ensures the client reconnects automatically on any drop.
> When the access token is refreshed, call `disconnectWebSocket()` then
> `connectWebSocket(newToken, ...)` to open a fresh authenticated session.

---

### 4. REST endpoints

#### 4.1 Initial page load — fetch split badge counts

```
GET /api/notifications/unread-counts
Authorization: Bearer <token>
```

Response:
```json
{
  "notificationCount": 3,
  "messageCount": 2
}
```

Call this once on app startup after authentication to seed the two badge counters.

#### 4.2 Paginated notification list (bell panel)

```
GET /api/notifications/my?page=0&size=20
Authorization: Bearer <token>
```

Response (PageResponse wrapper):
```json
{
  "content": [
    {
      "id": "uuid",
      "userId": "uuid",
      "type": "CONNECTION_REQUEST",
      "entityId": "uuid",
      "content": "John Doe sent you a connection request",
      "isRead": false,
      "createdAt": "2026-03-29T10:00:00"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 5,
  "totalPages": 1,
  "last": true
}
```

> **Important:** `isRead` is the correct JSON key (not `read`).

#### 4.3 Mark a single notification as read

```
PUT /api/notifications/{id}/read
Authorization: Bearer <token>
```

Call when the user clicks a specific notification item.
After the server processes this, a new count is pushed via WS
(`/queue/notifications/count`), so do NOT manually update the badge locally —
wait for the WS event.

#### 4.4 Mark all non-MESSAGE notifications as read ← Notification bell "Mark all" action

```
PUT /api/notifications/read-all
Authorization: Bearer <token>
```

This marks every notification **except MESSAGE type** as read.
Server pushes `{ notificationCount: 0, messageCount: <unchanged> }` via WS.
The notification bell badge drops to 0; the message badge is unaffected.

#### 4.5 Mark all MESSAGE notifications as read ← Messages panel open action

```
PUT /api/notifications/read-messages
Authorization: Bearer <token>
```

This marks every MESSAGE-type notification as read.
Server pushes `{ notificationCount: <unchanged>, messageCount: 0 }` via WS.
The message badge drops to 0; the notification bell badge is unaffected.

---

### 5. Sending a message via WebSocket (real-time path)

```js
stompClient.publish({
  destination: '/app/chat.send',
  body: JSON.stringify({
    conversationId: 'uuid',
    content: 'Hello!',
    jobId: null,            // optional: UUID of a job to share
  }),
});
```

After the server processes this:
1. The message is broadcast to `/topic/conversations/{conversationId}` — subscribe here
   to receive live messages in the open conversation.
2. The recipient(s) automatically get a `MESSAGE`-type notification pushed to their
   `/user/queue/notifications` subscription, and their `/user/queue/notifications/count`
   badge counts are updated — all handled server-side with no extra work from the sender.

You can also send a message via REST as a fallback:
```
POST /api/messages
Authorization: Bearer <token>
Content-Type: application/json

{ "conversationId": "uuid", "content": "Hello!", "jobId": null }
```

#### Subscribe to a conversation's live messages

```js
stompClient.subscribe(
  `/topic/conversations/${conversationId}`,
  (frame) => {
    const message = JSON.parse(frame.body);
    // { id, conversationId, senderId, senderEmail, senderPhotoUrl,
    //   content, jobId, jobTitle, isRead, createdAt }
    appendMessageToConversation(message);
  }
);
```

#### Subscribe to read-receipts

```js
stompClient.subscribe(
  `/topic/conversations/${conversationId}/read`,
  (frame) => {
    const receipt = JSON.parse(frame.body);
    // { conversationId, readByUserId }
    markConversationMessagesAsRead(receipt);
  }
);
```

#### Mark a conversation's messages as read

```
PUT /api/messages/read/{conversationId}
Authorization: Bearer <token>
```

Call this when the user opens a conversation panel. This marks all messages sent
by the other participant(s) as read and pushes a read-receipt to
`/topic/conversations/{conversationId}/read`.

> **Note:** marking a conversation as read via this REST endpoint does NOT automatically
> call the notification read-messages endpoint. You must call both:
> 1. `PUT /api/messages/read/{conversationId}` — marks the `Message` rows as read
> 2. `PUT /api/notifications/read-messages` — clears MESSAGE-type notification badges
> Call them together when the user opens the messages panel.

---

### 6. State management blueprint

```
AppState {
  badgeCounts: {
    notificationCount: number,   // bell badge
    messageCount: number,        // message button badge
  },
  notifications: Notification[],  // list shown in bell panel
}
```

#### On app init (after login):

```js
// 1. Fetch split counts from REST
const counts = await GET('/api/notifications/unread-counts');
store.setBadgeCounts(counts);

// 2. Connect WebSocket
connectWebSocket(accessToken,
  // onNotification — new incoming notification
  (notification) => {
    // Prepend to notification list if bell panel is open
    store.prependNotification(notification);
    // Do NOT manually increment badge here —
    // the server sends a count update on the same event
  },
  // onCountUpdate — authoritative badge counts from server
  (counts) => {
    store.setBadgeCounts(counts); // { notificationCount, messageCount }
  }
);
```

#### When user opens the Messages panel:

```js
// Clear the message badge
await PUT('/api/notifications/read-messages');
// Badge will drop to 0 via WS count push — no local state hack needed.
```

#### When user clicks "Mark all as read" on notification bell:

```js
await PUT('/api/notifications/read-all');
// Bell badge drops to 0 via WS count push.
// Message badge is unaffected.
```

#### When user opens a specific conversation:

```js
// 1. Fetch message history
const page = await GET(`/api/messages/${conversationId}?page=0&size=50`);

// 2. Mark messages read (clears unread dots in conversation)
await PUT(`/api/messages/read/${conversationId}`);

// 3. Clear message notification badges
await PUT('/api/notifications/read-messages');
```

---

### 7. Notification shape (TypeScript reference)

```ts
interface NotificationResponse {
  id: string;            // UUID
  userId: string;        // UUID  
  type: NotificationType;
  entityId: string | null; // UUID — e.g. messageId or applicationId
  content: string;
  isRead: boolean;       // KEY is "isRead" (not "read")
  createdAt: string;     // ISO-8601 datetime
}

type NotificationType =
  | 'MESSAGE'
  | 'CONNECTION_REQUEST'
  | 'CONNECTION_ACCEPTED'
  | 'JOB_APPLICATION'
  | 'APPLICATION_ACCEPTED'
  | 'APPLICATION_REJECTED';

interface NotificationCountResponse {
  notificationCount: number;  // unread bell-type notifications (non-MESSAGE)
  messageCount: number;       // unread MESSAGE-type notifications
}

interface MessageResponse {
  id: string;
  conversationId: string;
  senderId: string;
  senderEmail: string;
  senderPhotoUrl: string | null;
  content: string;
  jobId: string | null;
  jobTitle: string | null;
  isRead: boolean;       // KEY is "isRead" (not "read")
  createdAt: string;
}
```

---

### 8. Common pitfalls to avoid

| Pitfall | Correct behavior |
|---|---|
| Using `msg.read` instead of `msg.isRead` | JSON key is always `"isRead"` |
| Manually incrementing/decrementing badge counters | Always use the server-pushed `NotificationCountResponse` |
| Creating a new STOMP client on every token refresh | Call `deactivate()` then create and `activate()` a new client with the new token |
| Forgetting `heartbeatIncoming/Outgoing: 10000` | Without matching heartbeats, the server closes the session after 10 s of silence |
| Calling only `PUT /api/messages/read/{id}` when opening chat | Must also call `PUT /api/notifications/read-messages` to clear the message badge |
| Subscribing to `/user/${email}/queue/notifications` (email in path) | WRONG. Use `/user/queue/notifications` — no email. Spring resolves the session principal from the JWT automatically. Putting the email in the path breaks the route match and you receive nothing. |
| Not sending `Authorization` header in STOMP CONNECT | The server now rejects CONNECT frames with no valid JWT. The client will get a STOMP ERROR frame and auto-reconnect. Always pass `connectHeaders: { Authorization: \`Bearer \${token}\` }`. |
| Calling `PUT /api/notifications/read-all` from message button | That endpoint clears NON-message notifications. Use `read-messages` for the message button |

---

### 9. WebSocket destination quick reference

#### Client → Server (publish destinations)

| Destination | Payload |
|---|---|
| `/app/chat.send` | `{ conversationId, content, jobId? }` |

#### Client subscriptions (what the frontend subscribes to)

> ⚠️ User-specific subscriptions use `/user/queue/...` — **no email in the path**.
> Spring maps the session principal automatically.

| Subscribe path | Payload received |
|---|---|
| `/user/queue/notifications` | `NotificationResponse` — one new notification per event |
| `/user/queue/notifications/count` | `{ notificationCount, messageCount }` — authoritative badge counts |
| `/topic/conversations/{id}` | `MessageResponse` — new message in that conversation |
| `/topic/conversations/{id}/read` | `{ conversationId, readByUserId }` — read receipt |
