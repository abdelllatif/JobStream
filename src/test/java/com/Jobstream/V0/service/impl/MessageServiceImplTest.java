package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.request.MessageRequest;
import com.Jobstream.V0.dto.response.MessageResponse;
import com.Jobstream.V0.entity.Conversation;
import com.Jobstream.V0.entity.Message;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.repository.ConversationRepository;
import com.Jobstream.V0.repository.MessageRepository;
import com.Jobstream.V0.repository.UserRepository;
import com.Jobstream.V0.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private MessageServiceImpl messageService;

    @Test
    void sendMessage_Success() {
        UUID senderId = UUID.randomUUID();
        UUID conversationId = UUID.randomUUID();
        MessageRequest request = new MessageRequest();
        request.setConversationId(conversationId);
        request.setContent("Hello World");
        request.setJobId(null);

        Conversation conversation = Conversation.builder().id(conversationId).build();
        User sender = User.builder().id(senderId).build();

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(messageRepository.save(any(Message.class))).thenAnswer(i -> {
            Message m = i.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        MessageResponse response = messageService.sendMessage(senderId, request);

        assertNotNull(response);
        assertEquals("Hello World", response.getContent());
        verify(messageRepository).save(any(Message.class));
        verify(messagingTemplate).convertAndSend(eq("/queue/messages/" + conversationId), any(MessageResponse.class));
    }
}
