package com.job.websocket;

import com.job.entity.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final ConcurrentMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, String> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            sessions.put(session.getId(), session);
            userSessions.put(Long.parseLong(userId), session.getId());
            log.info("WebSocket connection established for user: {}", userId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        userSessions.entrySet().removeIf(entry -> entry.getValue().equals(session.getId()));
        log.info("WebSocket connection closed for session: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.debug("Received message: {}", payload);
    }

    public void sendNotificationToUser(Long userId, Notification notification) {
        String sessionId = userSessions.get(userId);
        if (sessionId != null) {
            WebSocketSession session = sessions.get(sessionId);
            if (session != null && session.isOpen()) {
                try {
                    String notificationJson = objectMapper.writeValueAsString(notification);
                    session.sendMessage(new TextMessage(notificationJson));
                    log.info("Sent notification to user {}: {}", userId, notification.getTitle());
                } catch (IOException e) {
                    log.error("Error sending notification to user {}: {}", userId, e.getMessage());
                }
            }
        }
    }

    public void sendMessageToUser(Long userId, String message) {
        String sessionId = userSessions.get(userId);
        if (sessionId != null) {
            WebSocketSession session = sessions.get(sessionId);
            if (session != null && session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                    log.info("Sent message to user {}: {}", userId, message);
                } catch (IOException e) {
                    log.error("Error sending message to user {}: {}", userId, e.getMessage());
                }
            }
        }
    }

    public void broadcastToAllUsers(String message) {
        sessions.values().parallelStream().forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    log.error("Error broadcasting message: {}", e.getMessage());
                }
            }
        });
    }

    public boolean isUserConnected(Long userId) {
        return userSessions.containsKey(userId);
    }

    public int getConnectedUsersCount() {
        return userSessions.size();
    }

    private String getUserIdFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null && query.contains("userId=")) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("userId=")) {
                    return param.substring(7);
                }
            }
        }
        return null;
    }
}
