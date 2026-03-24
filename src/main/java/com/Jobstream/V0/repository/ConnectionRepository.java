package com.Jobstream.V0.repository;

import com.Jobstream.V0.entity.Connection;
import com.Jobstream.V0.enums.ConnectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, UUID> {

    @Query("SELECT c FROM Connection c WHERE " +
           "(c.sender.id = :userId OR c.receiver.id = :userId) AND c.status = :status")
    List<Connection> findByUserIdAndStatus(@Param("userId") UUID userId,
                                           @Param("status") ConnectionStatus status);

    @Query("SELECT c FROM Connection c WHERE " +
           "(c.sender.id = :u1 AND c.receiver.id = :u2) OR " +
           "(c.sender.id = :u2 AND c.receiver.id = :u1)")
    Optional<Connection> findBetweenUsers(@Param("u1") UUID u1, @Param("u2") UUID u2);

    @Query("SELECT c FROM Connection c WHERE c.receiver.id = :userId AND c.status = 'PENDING'")
    List<Connection> findPendingReceivedByUser(@Param("userId") UUID userId);

    @Query("SELECT c FROM Connection c WHERE " +
           "(c.sender.id = :userId OR c.receiver.id = :userId) AND c.status = 'ACCEPTED'")
    List<Connection> findAcceptedConnectionsByUser(@Param("userId") UUID userId);

    boolean existsBySenderIdAndReceiverId(UUID senderId, UUID receiverId);
}
