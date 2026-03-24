package com.job.service.impl;

import com.job.entity.Connection;
import com.job.entity.User;
import com.job.entity.Connection.ConnectionStatus;
import com.job.repository.ConnectionRepository;
import com.job.repository.UserRepository;
import com.job.service.ConnectionService;
import com.job.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.job.mapper.UserMapper;
import com.job.dto.response.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionServiceImpl implements ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final com.job.websocket.NotificationBroadcaster notificationBroadcaster;
    private final AuthUtil authUtil;
    private final UserMapper userMapper;

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
            Connection connection = existingConnection.get();
            // If the previous connection request was rejected, allow the user to try again
            if (connection.getStatus() == ConnectionStatus.REJECTED) {
                connectionRepository.delete(connection);
                connectionRepository.flush();
            } else {
                throw new RuntimeException("Connection already exists or is blocked (Status: " + connection.getStatus() + ")");
            }
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

        // Notify receiver
        notificationBroadcaster.broadcastNotification(
                receiverId,
                "Demande de connexion",
                requester.getFirstName() + " " + requester.getLastName() + " souhaite se connecter avec vous.",
                com.job.enums.NotificationType.CONNECTION_REQUEST
        );

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

        // Integrity check: Only the receiver can accept the request
        Long currentUserId = authUtil.getCurrentUserId();
        if (!connection.getReceiver().getId().equals(currentUserId)) {
            throw new RuntimeException("Only the receiver can accept this connection request");
        }

        connection.setStatus(ConnectionStatus.ACCEPTED);
        connection.setAcceptedAt(LocalDateTime.now());
        connection.setUpdatedAt(LocalDateTime.now());

        Connection savedConnection = connectionRepository.save(connection);
        log.info("Connection request {} accepted", connectionId);

        // Notify requester
        notificationBroadcaster.broadcastNotification(
                connection.getRequester().getId(),
                "Invitation acceptée",
                connection.getReceiver().getFirstName() + " " + connection.getReceiver().getLastName() + " a accepté votre invitation.",
                com.job.enums.NotificationType.CONNECTION_REQUEST
        );

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
        connection.setBlockedBy(blocker);
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
    @Transactional
    public void deleteConnection(Long userId1, Long userId2) {
        Optional<Connection> connection = getConnectionBetweenUsers(userId1, userId2);
        connection.ifPresent(conn -> {
            connectionRepository.delete(conn);
            log.info("Connection between user {} and user {} deleted", userId1, userId2);
        });
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

    @Override
    public List<com.job.dto.response.UserNetworkResponseDTO> getNetworkUsers(Long currentUserId) {
        List<User> allUsers = userRepository.findAll();
        List<Connection> userConnections = connectionRepository.findByRequesterIdOrReceiverIdAndStatus(currentUserId,
                ConnectionStatus.ACCEPTED);
        List<Connection> sentRequests = connectionRepository.findByRequesterIdAndStatus(currentUserId,
                ConnectionStatus.PENDING);
        List<Connection> receivedRequests = connectionRepository.findByReceiverIdAndStatus(currentUserId,
                ConnectionStatus.PENDING);

        User currentUser = userRepository.findById(currentUserId).orElse(null);

        return allUsers.stream()
                .filter(user -> !user.getId().equals(currentUserId))
                .filter(user -> user.getRole() != com.job.enums.Role.ADMIN)
                .map(user -> {
                    String status = "NONE";

                    if (userConnections.stream().anyMatch(c -> c.getRequester().getId().equals(user.getId())
                            || c.getReceiver().getId().equals(user.getId()))) {
                        status = "CONNECTED";
                    } else if (sentRequests.stream().anyMatch(c -> c.getReceiver().getId().equals(user.getId()))) {
                        status = "SENT_PENDING";
                    } else if (receivedRequests.stream().anyMatch(c -> c.getRequester().getId().equals(user.getId()))) {
                        status = "PENDING";
                    }

                    String jobTitle = null;
                    if (user.getCandidateProfile() != null) {
                        jobTitle = user.getCandidateProfile().getJobTitle();
                    }

                    return com.job.dto.response.UserNetworkResponseDTO.builder()
                            .id(user.getId())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .profileImagePath(user.getProfilePicture())
                            .role(user.getRole().name())
                            .location(user.getLocation())
                            .bio(user.getBio())
                            .website(user.getWebsite())
                            .linkedinProfile(user.getLinkedinProfile())
                            .jobTitle(jobTitle)
                            .connectionStatus(status)
                            .build();
                })
                .sorted((u1, u2) -> {
                    // Sorting logic for suggestions
                    if (currentUser == null)
                        return 0;

                    // Connected users first
                    if ("CONNECTED".equals(u1.getConnectionStatus()) && !"CONNECTED".equals(u2.getConnectionStatus()))
                        return -1;
                    if (!"CONNECTED".equals(u1.getConnectionStatus()) && "CONNECTED".equals(u2.getConnectionStatus()))
                        return 1;

                    // Then Pending requests you received
                    if ("PENDING".equals(u1.getConnectionStatus()) && !"PENDING".equals(u2.getConnectionStatus()))
                        return -1;
                    if (!"PENDING".equals(u1.getConnectionStatus()) && "PENDING".equals(u2.getConnectionStatus()))
                        return 1;

                    // Then Sent pending requests
                    if ("SENT_PENDING".equals(u1.getConnectionStatus())
                            && !"SENT_PENDING".equals(u2.getConnectionStatus()))
                        return -1;
                    if (!"SENT_PENDING".equals(u1.getConnectionStatus())
                            && "SENT_PENDING".equals(u2.getConnectionStatus()))
                        return 1;

                    // Both NONE - Apply suggestion logic (Same Company)
                    if (currentUser.getCompanyProfile() != null) {
                        boolean u1SameCompany = currentUser.getCompanyProfile().getOwner().getId().equals(u1.getId()); // simplified
                                                                                                                       // logic
                                                                                                                       // for
                                                                                                                       // demo
                        boolean u2SameCompany = currentUser.getCompanyProfile().getOwner().getId().equals(u2.getId());

                        if (u1SameCompany && !u2SameCompany)
                            return -1;
                        if (!u1SameCompany && u2SameCompany)
                            return 1;
                    }

                    return 0;
                })
                .collect(Collectors.toList());
    }

    @Override
    public com.job.dto.response.ConnectionListResponseDTO getConnectionListForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Connection> allConnections = connectionRepository.findAllByRequesterIdOrReceiverId(userId, userId);

        List<com.job.dto.response.ConnectionDTO> connectionsDTO = allConnections.stream()
                .filter(c -> c.getStatus() != ConnectionStatus.REJECTED)
                .map(c -> {
                    User otherUser = c.getRequester().getId().equals(userId) ? c.getReceiver() : c.getRequester();
                    boolean blockedByMe = c.getStatus() == ConnectionStatus.BLOCKED && c.getBlockedBy() != null && c.getBlockedBy().getId().equals(userId);
                    String status = "pending";
                    if (c.getStatus() == ConnectionStatus.ACCEPTED) {
                        status = "connected";
                    } else if (c.getStatus() == ConnectionStatus.BLOCKED) {
                        status = "blocked";
                    }

                    return com.job.dto.response.ConnectionDTO.builder()
                            .userId(otherUser.getId())
                            .username(otherUser.getEmail())
                            .firstName(otherUser.getFirstName())
                            .lastName(otherUser.getLastName())
                            .profilePicture(otherUser.getProfilePicture())
                            .status(status)
                            .blockedByMe(blockedByMe)
                            .build();
                })
                .toList();

        List<Long> blockedUsers = allConnections.stream()
                .filter(c -> c.getStatus() == ConnectionStatus.BLOCKED && c.getBlockedBy() != null && c.getBlockedBy().getId().equals(userId))
                .map(c -> c.getRequester().getId().equals(userId) ? c.getReceiver().getId() : c.getRequester().getId())
                .toList();
        
        List<Long> usersWhoBlockedMe = allConnections.stream()
                .filter(c -> c.getStatus() == ConnectionStatus.BLOCKED && c.getBlockedBy() != null && !c.getBlockedBy().getId().equals(userId))
                .map(c -> c.getBlockedBy().getId())
                .toList();

        return com.job.dto.response.ConnectionListResponseDTO.builder()
                .connections(connectionsDTO)
                .blockedUsers(blockedUsers)
                .usersWhoBlockedMe(usersWhoBlockedMe)
                .currentUser(userMapper.toResponse(user))
                .build();
    }
}
