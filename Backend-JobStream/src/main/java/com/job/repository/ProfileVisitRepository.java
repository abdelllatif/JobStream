package com.job.repository;

import com.job.entity.ProfileVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProfileVisitRepository extends JpaRepository<ProfileVisit, Long> {

    List<ProfileVisit> findByProfileOwnerIdOrderByVisitedAtDesc(Long profileOwnerId);

    List<ProfileVisit> findByProfileOwnerIdAndVisitedAtBetweenOrderByVisitedAtDesc(
            Long profileOwnerId, LocalDateTime startDate, LocalDateTime endDate);

    long countByProfileOwnerId(Long profileOwnerId);

    @Query(value = "SELECT * FROM profile_visits WHERE profile_owner_id = :profileOwnerId ORDER BY visited_at DESC LIMIT :limit", 
           nativeQuery = true)
    List<ProfileVisit> findRecentVisits(@Param("profileOwnerId") Long profileOwnerId, @Param("limit") int limit);

    boolean existsByVisitorIdAndProfileOwnerId(Long visitorId, Long profileOwnerId);
}
