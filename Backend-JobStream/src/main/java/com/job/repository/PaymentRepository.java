package com.job.repository;

import com.job.entity.Payment;
import com.job.enums.PlanType;
import com.job.entity.Payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByUserIdOrderByPaidAtDesc(Long userId);
    
    Optional<Payment> findByPaypalTransactionId(String paypalTransactionId);
    
    Optional<Payment> findByPaypalOrderId(String paypalOrderId);
    
    Optional<Payment> findByPaypalPaymentId(String paypalPaymentId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    List<Payment> findByPlanType(PlanType planType);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    long countByStatus(@Param("status") PaymentStatus status);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED'")
    Double getTotalRevenue();
    
    @Query("SELECT p FROM Payment p WHERE p.user.id = :userId AND p.status = 'COMPLETED'")
    List<Payment> findCompletedPaymentsByUser(@Param("userId") Long userId);
}

