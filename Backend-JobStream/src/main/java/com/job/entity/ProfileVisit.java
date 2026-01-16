package com.job.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "profile_visits")
public class ProfileVisit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "visitor_id", nullable = false)
    private User visitor;

    @ManyToOne
    @JoinColumn(name = "profile_owner_id", nullable = false)
    private User profileOwner;

    private LocalDateTime visitedAt;

    private LocalDateTime createdAt;
}
