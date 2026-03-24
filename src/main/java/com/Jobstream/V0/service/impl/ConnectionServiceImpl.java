package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.request.ConnectionRequest;
import com.Jobstream.V0.dto.response.ConnectionResponse;
import com.Jobstream.V0.entity.Connection;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.enums.ConnectionStatus;
import com.Jobstream.V0.enums.NotificationType;
import com.Jobstream.V0.exception.*;
import com.Jobstream.V0.mapper.ConnectionMapper;
import com.Jobstream.V0.repository.ConnectionRepository;
import com.Jobstream.V0.repository.UserRepository;
import com.Jobstream.V0.service.ConnectionService;
import com.Jobstream.V0.service.NotificationService;
import com.Jobstream.V0.service.UserBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final UserBlockService userBlockService;

    @Override
    @Transactional
    public ConnectionResponse sendRequest(UUID senderId, ConnectionRequest request) {
        if (senderId.equals(request.getReceiverId())) {
            throw new BadRequestException("Cannot connect with yourself");
        }

        if (userBlockService.isBlocked(senderId, request.getReceiverId()) ||
                userBlockService.isBlocked(request.getReceiverId(), senderId)) {
            throw new BadRequestException("Cannot send request to this user");
        }

        connectionRepository.findBetweenUsers(senderId, request.getReceiverId())
                .ifPresent(c -> {
                    throw new DuplicateResourceException("Connection already exists");
                });

        User sender = findUser(senderId);
        User receiver = findUser(request.getReceiverId());

        Connection connection = Connection.builder()
                .sender(sender).receiver(receiver).status(ConnectionStatus.PENDING).build();
        connection = connectionRepository.save(connection);

        notificationService.createNotification(
                receiver, NotificationType.CONNECTION_REQUEST,
                connection.getId(),
                sender.getEmail() + " sent you a connection request"
        );

        return ConnectionMapper.toResponse(connection);
    }

    @Override
    @Transactional
    public ConnectionResponse accept(UUID connectionId, UUID userId) {
        Connection connection = findConnection(connectionId);
        assertReceiver(connection, userId);
        connection.setStatus(ConnectionStatus.ACCEPTED);
        connection = connectionRepository.save(connection);

        notificationService.createNotification(
                connection.getSender(), NotificationType.CONNECTION_ACCEPTED,
                connection.getId(),
                connection.getReceiver().getEmail() + " accepted your connection request"
        );

        return ConnectionMapper.toResponse(connection);
    }

    @Override
    @Transactional
    public ConnectionResponse reject(UUID connectionId, UUID userId) {
        Connection connection = findConnection(connectionId);
        assertReceiver(connection, userId);
        connection.setStatus(ConnectionStatus.REJECTED);
        return ConnectionMapper.toResponse(connectionRepository.save(connection));
    }

    @Override
    @Transactional
    public void remove(UUID connectionId, UUID userId) {
        Connection connection = findConnection(connectionId);
        boolean isParticipant = connection.getSender().getId().equals(userId) ||
                connection.getReceiver().getId().equals(userId);
        if (!isParticipant) throw new UnauthorizedException("Not your connection");
        connectionRepository.delete(connection);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConnectionResponse> getMyConnections(UUID userId) {
        return connectionRepository.findAcceptedConnectionsByUser(userId)
                .stream().map(ConnectionMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConnectionResponse> getPendingRequests(UUID userId) {
        return connectionRepository.findPendingReceivedByUser(userId)
                .stream().map(ConnectionMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ConnectionResponse getConnectionStatus(UUID userId, UUID otherUserId) {
        return connectionRepository.findBetweenUsers(userId, otherUserId)
                .map(ConnectionMapper::toResponse)
                .orElse(null);
    }

    private void assertReceiver(Connection connection, UUID userId) {
        if (!connection.getReceiver().getId().equals(userId)) {
            throw new UnauthorizedException("Not the receiver of this request");
        }
    }

    private Connection findConnection(UUID id) {
        return connectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Connection", "id", id));
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}
