package com.job.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Data
@Table(name = "educations")
public class Education {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String school;
    private String degree;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "candidate_profile_id")
    private CandidateProfile candidateProfile;
}
