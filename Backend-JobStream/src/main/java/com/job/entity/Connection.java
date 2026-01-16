package com.job.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "connections")
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    private ConnectionStatus status;

    private LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum ConnectionStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        BLOCKED
    }
}
