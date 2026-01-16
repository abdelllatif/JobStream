package com.job.controller;

import com.job.entity.Message;
import com.job.service.MessageService;
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

    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<Message> sendMessage(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam String content,
            @RequestParam(required = false) Long jobId) {
        try {
            Message message;
            if (jobId != null) {
                message = messageService.sendMessage(senderId, receiverId, content, jobId);
            } else {
                message = messageService.sendMessage(senderId, receiverId, content);
            }
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            log.error("Error sending message from {} to {}: {}", senderId, receiverId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{messageId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Message> getMessage(@PathVariable Long messageId) {
        Optional<Message> message = messageService.getMessage(messageId);
        return message.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/conversation/{userId1}/{userId2}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<Message>> getConversation(
            @PathVariable Long userId1,
            @PathVariable Long userId2) {
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
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageService.getConversationPaginated(userId1, userId2, pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<Message>> getUserMessages(@PathVariable Long userId) {
        List<Message> messages = messageService.getUserMessages(userId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/unread/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<Message>> getUnreadMessages(@PathVariable Long userId) {
        List<Message> messages = messageService.getUnreadMessages(userId);
        return ResponseEntity.ok(messages);
    }

    @PutMapping("/read/{messageId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> markAsRead(@PathVariable Long messageId) {
        try {
            messageService.markAsRead(messageId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error marking message {} as read: {}", messageId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/read-conversation/{userId1}/{userId2}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> markConversationAsRead(
            @PathVariable Long userId1,
            @PathVariable Long userId2) {
        try {
            messageService.markConversationAsRead(userId1, userId2);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error marking conversation between {} and {} as read: {}", userId1, userId2, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{messageId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId) {
        try {
            messageService.deleteMessage(messageId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting message {}: {}", messageId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/conversation/{userId1}/{userId2}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Void> deleteConversation(
            @PathVariable Long userId1,
            @PathVariable Long userId2) {
        try {
            messageService.deleteConversation(userId1, userId2);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting conversation between {} and {}: {}", userId1, userId2, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/unread-count/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long userId) {
        long count = messageService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/partners/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<Long>> getConversationPartners(@PathVariable Long userId) {
        List<Long> partners = messageService.getConversationPartners(userId);
        return ResponseEntity.ok(partners);
    }
}
