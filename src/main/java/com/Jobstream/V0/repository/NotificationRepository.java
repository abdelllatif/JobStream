package com.Jobstream.V0.repository;

import com.Jobstream.V0.entity.Notification;
import com.Jobstream.V0.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    long countByUserIdAndIsReadFalse(UUID userId);

    long countByUserIdAndIsReadFalseAndType(UUID userId, NotificationType type);

    long countByUserIdAndIsReadFalseAndTypeNot(UUID userId, NotificationType type);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsRead(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true " +
           "WHERE n.user.id = :userId AND n.type != :type AND n.isRead = false")
    int markAllAsReadExcludingType(@Param("userId") UUID userId,
                                   @Param("type") NotificationType type);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true " +
           "WHERE n.user.id = :userId AND n.type = :type AND n.isRead = false")
    int markAllAsReadByType(@Param("userId") UUID userId,
                            @Param("type") NotificationType type);
}
