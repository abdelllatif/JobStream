package com.Jobstream.V0.service;

import com.Jobstream.V0.dto.request.ConnectionRequest;
import com.Jobstream.V0.dto.response.ConnectionResponse;

import java.util.List;
import java.util.UUID;

public interface ConnectionService {

    ConnectionResponse sendRequest(UUID senderId, ConnectionRequest request);

    ConnectionResponse accept(UUID connectionId, UUID userId);

    ConnectionResponse reject(UUID connectionId, UUID userId);

    void remove(UUID connectionId, UUID userId);

    List<ConnectionResponse> getMyConnections(UUID userId);

    List<ConnectionResponse> getPendingRequests(UUID userId);

    ConnectionResponse getConnectionStatus(UUID userId, UUID otherUserId);
}
