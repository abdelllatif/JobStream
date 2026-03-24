package com.job.repository;

import com.job.entity.Connection;
import com.job.entity.Connection.ConnectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    @Query("SELECT c FROM Connection c WHERE (c.requester.id = :userId OR c.receiver.id = :userId) AND c.status = :status")
    List<Connection> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ConnectionStatus status);

    List<Connection> findByRequesterIdAndStatus(Long requesterId, ConnectionStatus status);

    List<Connection> findByReceiverIdAndStatus(Long receiverId, ConnectionStatus status);

    @Query("SELECT c FROM Connection c WHERE (c.requester.id = :userId1 AND c.receiver.id = :userId2) OR (c.requester.id = :userId2 AND c.receiver.id = :userId1)")
    Optional<Connection> findConnectionBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT c FROM Connection c WHERE (c.requester.id = :userId OR c.receiver.id = :userId) AND c.status = :status")
    List<Connection> findByRequesterIdOrReceiverIdAndStatus(@Param("userId") Long userId, @Param("status") ConnectionStatus status);

    @Query("SELECT COUNT(c) FROM Connection c WHERE c.status = :status")
    long countByStatus(@Param("status") ConnectionStatus status);

    boolean existsByRequesterIdAndReceiverId(Long requesterId, Long receiverId);

    @Query("SELECT c FROM Connection c WHERE c.requester.id = :userId1 OR c.receiver.id = :userId2")
    List<Connection> findAllByRequesterIdOrReceiverId(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}
