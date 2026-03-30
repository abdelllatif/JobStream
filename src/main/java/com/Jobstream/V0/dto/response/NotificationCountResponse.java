package com.Jobstream.V0.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pushed to /user/{email}/queue/notifications/count via WebSocket
 * and returned from GET /api/notifications/unread-counts.
 *
 * notificationCount = unread notifications whose type is NOT MESSAGE
 * messageCount      = unread notifications whose type IS  MESSAGE
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCountResponse {
    private long notificationCount;
    private long messageCount;
}
