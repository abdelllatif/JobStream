package com.job.repository;

import com.job.entity.User;
import com.job.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByGoogleId(String googleId);
    boolean existsByGoogleId(String googleId);
    
    List<User> findByRole(Role role);
    
    @Query("SELECT u FROM User u WHERE u.premiumUser = true")
    List<User> findPremiumUsers();
    
    @Query("SELECT u FROM User u WHERE u.emailVerified = true")
    List<User> findVerifiedUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") Role role);
}
