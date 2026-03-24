package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.request.ConnectionRequest;
import com.Jobstream.V0.dto.response.ConnectionResponse;
import com.Jobstream.V0.entity.Connection;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.enums.ConnectionStatus;
import com.Jobstream.V0.exception.DuplicateResourceException;
import com.Jobstream.V0.repository.ConnectionRepository;
import com.Jobstream.V0.repository.UserRepository;
import com.Jobstream.V0.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConnectionServiceImplTest {

    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ConnectionServiceImpl connectionService;

    @Test
    void sendConnectionRequest_Success() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        ConnectionRequest request = new ConnectionRequest();
        request.setReceiverId(receiverId);

        when(userRepository.findById(senderId)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(new User()));
        when(connectionRepository.findBetweenUsers(senderId, receiverId)).thenReturn(Optional.empty());
        when(connectionRepository.save(any(Connection.class))).thenAnswer(i -> {
            Connection c = i.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        ConnectionResponse response = connectionService.sendRequest(senderId, request);

        assertNotNull(response);
        assertEquals(ConnectionStatus.PENDING, response.getStatus());
        verify(connectionRepository).save(any(Connection.class));
    }

    @Test
    void acceptConnectionRequest_Success() {
        UUID connectionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Connection connection = Connection.builder()
                .id(connectionId)
                .status(ConnectionStatus.PENDING)
                .sender(User.builder().id(UUID.randomUUID()).build())
                .receiver(User.builder().id(userId).build())
                .build();

        when(connectionRepository.findById(connectionId)).thenReturn(Optional.of(connection));
        when(connectionRepository.save(any(Connection.class))).thenAnswer(i -> i.getArguments()[0]);

        ConnectionResponse response = connectionService.accept(connectionId, userId);

        assertNotNull(response);
        assertEquals(ConnectionStatus.ACCEPTED, response.getStatus());
        verify(connectionRepository).save(any(Connection.class));
    }
}
