package com.job.repository;

import com.job.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE (m.sender.id = :userId1 AND m.receiver.id = :userId2) OR (m.sender.id = :userId2 AND m.receiver.id = :userId1) ORDER BY m.sentAt DESC")
    List<Message> findConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT m FROM Message m WHERE (m.sender.id = :userId1 AND m.receiver.id = :userId2) OR (m.sender.id = :userId2 AND m.receiver.id = :userId1) ORDER BY m.sentAt DESC")
    Page<Message> findConversationPaginated(@Param("userId1") Long userId1, @Param("userId2") Long userId2, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId ORDER BY m.sentAt DESC")
    List<Message> findByUserIdOrderBySentAtDesc(@Param("userId") Long userId);

    List<Message> findByReceiverIdAndReadFalseOrderBySentAtDesc(Long receiverId);

    @Query("SELECT m FROM Message m WHERE (m.sender.id = :userId1 AND m.receiver.id = :userId2) OR (m.sender.id = :userId2 AND m.receiver.id = :userId1) AND m.read = false")
    List<Message> findUnreadMessages(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.id = :userId AND m.read = false")
    long countByReceiverIdAndReadFalse(@Param("userId") Long userId);

    @Query("SELECT DISTINCT CASE WHEN m.sender.id = :userId THEN m.receiver.id ELSE m.sender.id END FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    List<Long> findConversationPartners(@Param("userId") Long userId);

    void deleteBySenderIdOrReceiverId(Long senderId, Long receiverId);
}

