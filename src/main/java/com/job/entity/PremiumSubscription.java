package com.job.entity;

import com.job.enums.PlanType;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "premium_subscriptions")
public class PremiumSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private PlanType planType;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;

    private BigDecimal amount;
    private String currency;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
