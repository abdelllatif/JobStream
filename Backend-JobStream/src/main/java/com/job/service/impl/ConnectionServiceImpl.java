package com.job.service.impl;

import com.job.entity.Connection;
import com.job.entity.User;
import com.job.entity.Connection.ConnectionStatus;
import com.job.repository.ConnectionRepository;
import com.job.repository.UserRepository;
import com.job.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionServiceImpl implements ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Connection sendConnectionRequest(Long requesterId, Long receiverId) {
        if (requesterId.equals(receiverId)) {
            throw new RuntimeException("Cannot send connection request to yourself");
        }

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Requester not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Optional<Connection> existingConnection = getConnectionBetweenUsers(requesterId, receiverId);
        if (existingConnection.isPresent()) {
            throw new RuntimeException("Connection already exists");
        }

        Connection connection = new Connection();
        connection.setRequester(requester);
        connection.setReceiver(receiver);
        connection.setStatus(ConnectionStatus.PENDING);
        connection.setRequestedAt(LocalDateTime.now());
        connection.setCreatedAt(LocalDateTime.now());
        connection.setUpdatedAt(LocalDateTime.now());

        Connection savedConnection = connectionRepository.save(connection);
        log.info("Connection request sent from user {} to user {}", requesterId, receiverId);
        return savedConnection;
    }

    @Override
    @Transactional
    public Connection acceptConnectionRequest(Long connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection not found"));

        if (connection.getStatus() != ConnectionStatus.PENDING) {
            throw new RuntimeException("Connection is not pending");
        }

        connection.setStatus(ConnectionStatus.ACCEPTED);
        connection.setAcceptedAt(LocalDateTime.now());
        connection.setUpdatedAt(LocalDateTime.now());

        Connection savedConnection = connectionRepository.save(connection);
        log.info("Connection request {} accepted", connectionId);
        return savedConnection;
    }

    @Override
    @Transactional
    public Connection rejectConnectionRequest(Long connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection not found"));

        if (connection.getStatus() != ConnectionStatus.PENDING) {
            throw new RuntimeException("Connection is not pending");
        }

        connection.setStatus(ConnectionStatus.REJECTED);
        connection.setUpdatedAt(LocalDateTime.now());

        Connection savedConnection = connectionRepository.save(connection);
        log.info("Connection request {} rejected", connectionId);
        return savedConnection;
    }

    @Override
    @Transactional
    public void blockUser(Long blockerId, Long blockedId) {
        if (blockerId.equals(blockedId)) {
            throw new RuntimeException("Cannot block yourself");
        }

        User blocker = userRepository.findById(blockerId)
                .orElseThrow(() -> new RuntimeException("Blocker not found"));
        User blocked = userRepository.findById(blockedId)
                .orElseThrow(() -> new RuntimeException("Blocked user not found"));

        Optional<Connection> existingConnection = getConnectionBetweenUsers(blockerId, blockedId);
        
        Connection connection = existingConnection.orElse(new Connection());
        connection.setRequester(blocker);
        connection.setReceiver(blocked);
        connection.setStatus(ConnectionStatus.BLOCKED);
        connection.setUpdatedAt(LocalDateTime.now());
        
        if (connection.getCreatedAt() == null) {
            connection.setCreatedAt(LocalDateTime.now());
        }

        connectionRepository.save(connection);
        log.info("User {} blocked user {}", blockerId, blockedId);
    }

    @Override
    @Transactional
    public void unblockUser(Long blockerId, Long blockedId) {
        Optional<Connection> connection = getConnectionBetweenUsers(blockerId, blockedId);
        if (connection.isPresent() && connection.get().getStatus() == ConnectionStatus.BLOCKED) {
            connectionRepository.delete(connection.get());
            log.info("User {} unblocked user {}", blockerId, blockedId);
        }
    }

    @Override
    public List<Connection> getUserConnections(Long userId) {
        return connectionRepository.findByUserIdAndStatus(userId, ConnectionStatus.ACCEPTED);
    }

    @Override
    public List<Connection> getPendingRequests(Long userId) {
        return connectionRepository.findByReceiverIdAndStatus(userId, ConnectionStatus.PENDING);
    }

    @Override
    public List<Connection> getSentRequests(Long userId) {
        return connectionRepository.findByRequesterIdAndStatus(userId, ConnectionStatus.PENDING);
    }

    @Override
    public Optional<Connection> getConnectionBetweenUsers(Long userId1, Long userId2) {
        return connectionRepository.findConnectionBetweenUsers(userId1, userId2);
    }

    @Override
    public boolean areUsersConnected(Long userId1, Long userId2) {
        return getConnectionBetweenUsers(userId1, userId2)
                .map(connection -> connection.getStatus() == ConnectionStatus.ACCEPTED)
                .orElse(false);
    }
}
