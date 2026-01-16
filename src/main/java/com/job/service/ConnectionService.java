package com.job.service;

import com.job.entity.Connection;
import com.job.entity.Connection.ConnectionStatus;

import java.util.List;
import java.util.Optional;

public interface ConnectionService {
    Connection sendConnectionRequest(Long requesterId, Long receiverId);
    Connection acceptConnectionRequest(Long connectionId);
    Connection rejectConnectionRequest(Long connectionId);
    void blockUser(Long blockerId, Long blockedId);
    void unblockUser(Long blockerId, Long blockedId);
    List<Connection> getUserConnections(Long userId);
    List<Connection> getPendingRequests(Long userId);
    List<Connection> getSentRequests(Long userId);
    Optional<Connection> getConnectionBetweenUsers(Long userId1, Long userId2);
    boolean areUsersConnected(Long userId1, Long userId2);
}
