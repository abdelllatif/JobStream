package com.job.service.impl;

import com.job.entity.Job;
import com.job.entity.Message;
import com.job.entity.User;
import com.job.repository.JobRepository;
import com.job.repository.MessageRepository;
import com.job.repository.UserRepository;
import com.job.service.ConnectionService;
import com.job.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ConnectionService connectionService;
    private final com.job.websocket.NotificationBroadcaster notificationBroadcaster;

    @Override
    @Transactional
    public Message sendMessage(Long senderId, Long receiverId, String content, Long jobId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (!connectionService.areUsersConnected(senderId, receiverId)) {
            throw new RuntimeException("Users must be connected to send messages");
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setRead(false);
        message.setSentAt(LocalDateTime.now());
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());

        if (jobId != null) {
            Job job = jobRepository.findById(jobId).orElse(null);
            message.setJob(job);
        }

        Message savedMessage = messageRepository.save(message);
        log.info("Message sent from user {} to user {}", senderId, receiverId);

        // Notify receiver
        notificationBroadcaster.broadcastNotification(
                receiverId,
                "Nouveau message",
                sender.getFirstName() + " " + sender.getLastName() + " vous a envoyé un message.",
                com.job.enums.NotificationType.MESSAGE_RECEIVED
        );

        return savedMessage;
    }

    @Override
    @Transactional
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        return sendMessage(senderId, receiverId, content, null);
    }

    @Override
    public Optional<Message> getMessage(Long messageId) {
        return messageRepository.findById(messageId);
    }

    @Override
    public List<Message> getConversation(Long userId1, Long userId2) {
        return messageRepository.findConversation(userId1, userId2);
    }

    @Override
    public Page<Message> getConversationPaginated(Long userId1, Long userId2, Pageable pageable) {
        return messageRepository.findConversationPaginated(userId1, userId2, pageable);
    }

    @Override
    public List<Message> getUserMessages(Long userId) {
        return messageRepository.findByUserIdOrderBySentAtDesc(userId);
    }

    @Override
    public List<Message> getUnreadMessages(Long userId) {
        return messageRepository.findByReceiverIdAndReadFalseOrderBySentAtDesc(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        message.setRead(true);
        message.setReadAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());

        messageRepository.save(message);
        log.info("Marked message {} as read", messageId);
    }

    @Override
    @Transactional
    public void markConversationAsRead(Long userId1, Long userId2) {
        List<Message> unreadMessages = messageRepository.findUnreadMessages(userId1, userId2);
        unreadMessages.forEach(message -> {
            message.setRead(true);
            message.setReadAt(LocalDateTime.now());
            message.setUpdatedAt(LocalDateTime.now());
        });
        messageRepository.saveAll(unreadMessages);
        log.info("Marked conversation between users {} and {} as read", userId1, userId2);
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId) {
        messageRepository.deleteById(messageId);
        log.info("Deleted message {}", messageId);
    }

    @Override
    @Transactional
    public void deleteConversation(Long userId1, Long userId2) {
        List<Message> conversation = getConversation(userId1, userId2);
        messageRepository.deleteAll(conversation);
        log.info("Deleted conversation between users {} and {}", userId1, userId2);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return messageRepository.countByReceiverIdAndReadFalse(userId);
    }

    @Override
    public List<Long> getConversationPartners(Long userId) {
        return messageRepository.findConversationPartners(userId);
    }
}
