package com.job.controller;

import com.job.dto.request.MessageCreateRequest;
import com.job.dto.response.ConversationPartnerDTO;
import com.job.dto.response.MessageResponseDTO;
import com.job.entity.Message;
import com.job.entity.User;
import com.job.repository.UserRepository;
import com.job.service.MessageService;
import com.job.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    private MessageResponseDTO toDto(Message message) {
        return MessageResponseDTO.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .receiverId(message.getReceiver().getId())
                .content(message.getContent())
                .jobId(message.getJob() != null ? message.getJob().getId() : null)
                .createdAt(message.getSentAt() != null
                        ? message.getSentAt().toString()
                        : (message.getCreatedAt() != null ? message.getCreatedAt().toString() : null))
                .read(message.isRead())
                .build();
    }

    /**
     * Frontend-friendly endpoint:
     * POST /api/messages
     * Body: { senderId, receiverId, content, jobId? }
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<MessageResponseDTO> createMessage(@RequestBody MessageCreateRequest request) {
        Message message = (request.getJobId() != null)
                ? messageService.sendMessage(request.getSenderId(), request.getReceiverId(),
                        request.getContent(), request.getJobId())
                : messageService.sendMessage(request.getSenderId(), request.getReceiverId(),
                        request.getContent());
        return ResponseEntity.ok(toDto(message));
    }

    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<Message> sendMessage(
            @RequestParam Long receiverId,
            @RequestParam String content,
            @RequestParam(required = false) Long jobId) {
        Long senderId = authUtil.getCurrentUserId();
        Message message = (jobId != null)
                ? messageService.sendMessage(senderId, receiverId, content, jobId)
                : messageService.sendMessage(senderId, receiverId, content);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{messageId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<MessageResponseDTO> getMessage(@PathVariable Long messageId) {
        Optional<Message> message = messageService.getMessage(messageId);
        return message.map(value -> ResponseEntity.ok(toDto(value)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Frontend-friendly conversation endpoint:
     * GET /api/messages/conversation?u1=&u2=
     */
    @GetMapping("/conversation")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<MessageResponseDTO>> getConversationByQuery(
            @RequestParam("u2") Long otherUserId) {
        Long currentUserId = authUtil.getCurrentUserId();
        List<Message> messages = messageService.getConversation(currentUserId, otherUserId);
        List<MessageResponseDTO> dtoList = messages.stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/conversation/{userId1}/{userId2}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<Message>> getConversation(
            @PathVariable Long userId1,
            @PathVariable Long userId2) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId1) && !currentUserId.equals(userId2)) {
            return ResponseEntity.badRequest().build();
        }
        List<Message> messages = messageService.getConversation(userId1, userId2);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/conversation/{userId1}/{userId2}/paginated")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Page<Message>> getConversationPaginated(
            @PathVariable Long userId1,
            @PathVariable Long userId2,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId1) && !currentUserId.equals(userId2)) {
            return ResponseEntity.badRequest().build();
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageService.getConversationPaginated(userId1, userId2, pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<MessageResponseDTO>> getUserMessages(@PathVariable Long userId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        List<Message> messages = messageService.getUserMessages(userId);
        List<MessageResponseDTO> dtoList = messages.stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/unread/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<MessageResponseDTO>> getUnreadMessages(@PathVariable Long userId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        List<Message> messages = messageService.getUnreadMessages(userId);
        List<MessageResponseDTO> dtoList = messages.stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @PutMapping("/read/{messageId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> markAsRead(@PathVariable Long messageId) {
        messageService.markAsRead(messageId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mark-read/{messageId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> markAsReadPost(@PathVariable Long messageId) {
        messageService.markAsRead(messageId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-conversation/{userId1}/{userId2}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> markConversationAsRead(
            @PathVariable Long userId1,
            @PathVariable Long userId2) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId1) && !currentUserId.equals(userId2)) {
            return ResponseEntity.badRequest().build();
        }
        messageService.markConversationAsRead(userId1, userId2);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mark-conversation-read")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> markConversationAsReadPost(
            @RequestParam("u2") Long otherUserId) {
        Long currentUserId = authUtil.getCurrentUserId();
        messageService.markConversationAsRead(currentUserId, otherUserId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{messageId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/conversation/{userId1}/{userId2}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> deleteConversation(
            @PathVariable Long userId1,
            @PathVariable Long userId2) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId1) && !currentUserId.equals(userId2)) {
            return ResponseEntity.badRequest().build();
        }
        messageService.deleteConversation(userId1, userId2);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-count/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long userId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        long count = messageService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/partners/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<ConversationPartnerDTO>> getConversationPartners(@PathVariable Long userId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        List<Long> partnerIds = messageService.getConversationPartners(userId);
        List<ConversationPartnerDTO> partners = partnerIds.stream()
                .map(id -> userRepository.findById(id).orElse(null))
                .filter(user -> user != null)
                .map(this::toPartnerDto)
                .toList();
        return ResponseEntity.ok(partners);
    }

    private ConversationPartnerDTO toPartnerDto(User user) {
        return ConversationPartnerDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .profilePicture(user.getProfilePicture())
                .build();
    }
}
