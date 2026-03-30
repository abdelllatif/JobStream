package com.Jobstream.V0.repository;

import com.Jobstream.V0.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(u.profile.headline) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND u.id != :currentUserId " +
           "AND u.role != com.Jobstream.V0.enums.Role.ADMIN " +
           "AND u.id NOT IN (SELECT ub.blocked.id FROM UserBlock ub WHERE ub.blocker.id = :currentUserId) " +
           "AND u.id NOT IN (SELECT ub.blocker.id FROM UserBlock ub WHERE ub.blocked.id = :currentUserId)")
    Page<User> searchUsers(@Param("query") String query, @Param("currentUserId") UUID currentUserId, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.id != :currentUserId " +
            "AND u.role != com.Jobstream.V0.enums.Role.ADMIN " +
            "AND u.id NOT IN (SELECT ub.blocked.id FROM UserBlock ub WHERE ub.blocker.id = :currentUserId) " +
            "AND u.id NOT IN (SELECT ub.blocker.id FROM UserBlock ub WHERE ub.blocked.id = :currentUserId)")
    Page<User> findNetworkUsers(@Param("currentUserId") UUID currentUserId, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role != com.Jobstream.V0.enums.Role.ADMIN ORDER BY u.createdAt DESC")
    Page<User> findAllExcludingAdmins(Pageable pageable);
}
