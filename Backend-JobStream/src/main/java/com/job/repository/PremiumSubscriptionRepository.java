package com.job.repository;

import com.job.entity.PremiumSubscription;
import com.job.enums.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PremiumSubscriptionRepository extends JpaRepository<PremiumSubscription, Long> {

    Optional<PremiumSubscription> findByUserIdAndActiveTrue(Long userId);

    List<PremiumSubscription> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT ps FROM PremiumSubscription ps WHERE ps.active = true AND ps.endDate < :now")
    List<PremiumSubscription> findByActiveTrueAndEndDateBefore(@Param("now") LocalDateTime now);

    List<PremiumSubscription> findByPlanType(PlanType planType);

    @Query("SELECT COUNT(ps) FROM PremiumSubscription ps WHERE ps.active = true AND ps.planType = :planType")
    long countActiveSubscriptionsByPlanType(@Param("planType") PlanType planType);
}
