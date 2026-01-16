package com.job.entity;

import jakarta.persistence.*;
@Entity
@Table(name = "skills")
public class Skill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int level;

    @ManyToOne
    @JoinColumn(name = "candidate_profile_id")
    private CandidateProfile candidateProfile;
}
