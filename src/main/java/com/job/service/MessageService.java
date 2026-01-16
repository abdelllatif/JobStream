package com.job.service;

import com.job.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MessageService {
    Message sendMessage(Long senderId, Long receiverId, String content, Long jobId);
    Message sendMessage(Long senderId, Long receiverId, String content);
    Optional<Message> getMessage(Long messageId);
    List<Message> getConversation(Long userId1, Long userId2);
    Page<Message> getConversationPaginated(Long userId1, Long userId2, Pageable pageable);
    List<Message> getUserMessages(Long userId);
    List<Message> getUnreadMessages(Long userId);
    void markAsRead(Long messageId);
    void markConversationAsRead(Long userId1, Long userId2);
    void deleteMessage(Long messageId);
    void deleteConversation(Long userId1, Long userId2);
    long getUnreadCount(Long userId);
    List<Long> getConversationPartners(Long userId);
}
