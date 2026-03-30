package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.request.ConversationRequest;
import com.Jobstream.V0.dto.response.ConversationResponse;
import com.Jobstream.V0.dto.response.MessageResponse;
import com.Jobstream.V0.dto.response.UserResponse;
import com.Jobstream.V0.entity.Conversation;
import com.Jobstream.V0.entity.ConversationParticipant;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.exception.UnauthorizedException;
import com.Jobstream.V0.mapper.MessageMapper;
import com.Jobstream.V0.mapper.UserMapper;
import com.Jobstream.V0.repository.*;
import com.Jobstream.V0.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getMyConversations(UUID userId) {
        return conversationRepository.findByParticipantUserId(userId)
                .stream()
                .map(c -> buildConversationResponse(c, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ConversationResponse findOrCreateDirectConversation(UUID userId, ConversationRequest request) {
        return conversationRepository
                .findDirectConversation(userId, request.getTargetUserId())
                .map(c -> buildConversationResponse(c, userId))
                .orElseGet(() -> createNewConversation(userId, request.getTargetUserId()));
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationResponse getById(UUID conversationId, UUID userId) {
        Conversation conversation = findConversation(conversationId);
        if (!participantRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            throw new UnauthorizedException("Not a participant in this conversation");
        }
        return buildConversationResponse(conversation, userId);
    }

    private ConversationResponse createNewConversation(UUID userId, UUID targetUserId) {
        User user = findUser(userId);
        User target = findUser(targetUserId);

        Conversation conversation = conversationRepository.save(new Conversation());

        ConversationParticipant p1 = ConversationParticipant.builder()
                .conversation(conversation).user(user).build();
        ConversationParticipant p2 = ConversationParticipant.builder()
                .conversation(conversation).user(target).build();
        participantRepository.save(p1);
        participantRepository.save(p2);

        return buildConversationResponse(conversation, userId);
    }

    private ConversationResponse buildConversationResponse(Conversation conversation, UUID currentUserId) {
        List<UserResponse> participants = conversation.getParticipants()
                .stream()
                .map(p -> UserMapper.toResponse(p.getUser()))
                .collect(Collectors.toList());

        MessageResponse lastMessage = messageRepository
                .findLatestMessage(conversation.getId(), PageRequest.of(0, 1))
                .stream().map(MessageMapper::toResponse).findFirst().orElse(null);

        long unreadCount = messageRepository.countByConversationIdAndIsReadFalseAndSenderIdNot(
                conversation.getId(), currentUserId);

        return ConversationResponse.builder()
                .id(conversation.getId())
                .participants(participants)
                .lastMessage(lastMessage)
                .unreadCount(unreadCount)
                .createdAt(conversation.getCreatedAt())
                .build();
    }

    private Conversation findConversation(UUID id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", id));
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}
