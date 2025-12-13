package com.job.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime paidAt;

    private String paypalTransactionId;

    @Column(columnDefinition = "boolean default false")
    private boolean success = false;
}