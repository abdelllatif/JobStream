package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.request.MessageRequest;
import com.Jobstream.V0.dto.response.MessageResponse;
import com.Jobstream.V0.dto.response.PageResponse;
import com.Jobstream.V0.entity.Conversation;
import com.Jobstream.V0.entity.Job;
import com.Jobstream.V0.entity.Message;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.enums.NotificationType;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.exception.UnauthorizedException;
import com.Jobstream.V0.mapper.MessageMapper;
import com.Jobstream.V0.repository.*;
import com.Jobstream.V0.service.MessageService;
import com.Jobstream.V0.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public MessageResponse sendMessage(UUID senderId, MessageRequest request) {
        Conversation conversation = findConversation(request.getConversationId());
        User sender = findUser(senderId);

        if (!participantRepository.existsByConversationIdAndUserId(request.getConversationId(), senderId)) {
            throw new UnauthorizedException("Not a participant in this conversation");
        }

        Job job = resolveJob(request.getJobId());

        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(request.getContent())
                .job(job)
                .isRead(false)
                .build();
        final Message savedMessage = messageRepository.save(message);

        MessageResponse response = MessageMapper.toResponse(savedMessage);

        // Real-time broadcast
        messagingTemplate.convertAndSend(
                "/queue/messages/" + conversation.getId(), response);

        // Notify other participants
        conversation.getParticipants().stream()
                .filter(p -> !p.getUser().getId().equals(senderId))
                .forEach(p -> notificationService.createNotification(
                        p.getUser(), NotificationType.MESSAGE,
                        savedMessage.getId(),
                        sender.getEmail() + ": " + truncate(request.getContent(), 80)
                ));

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MessageResponse> getMessages(UUID conversationId, UUID userId, Pageable pageable) {
        if (!participantRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            throw new UnauthorizedException("Not a participant in this conversation");
        }
        Page<Message> page = messageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId, pageable);
        return PageResponse.<MessageResponse>builder()
                .content(page.getContent().stream().map(MessageMapper::toResponse).collect(Collectors.toList()))
                .page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages())
                .last(page.isLast()).build();
    }

    @Override
    @Transactional
    public int markConversationAsRead(UUID conversationId, UUID userId) {
        if (!participantRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            throw new UnauthorizedException("Not a participant");
        }
        return messageRepository.markAllAsRead(conversationId, userId);
    }

    private String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }

    private Conversation findConversation(UUID id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", id));
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private Job resolveJob(UUID jobId) {
        if (jobId == null) return null;
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
    }
}
