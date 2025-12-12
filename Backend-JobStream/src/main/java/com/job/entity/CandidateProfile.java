package com.job.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "candidate_profiles")
public class CandidateProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String phone;
    private String address;
    private String summary;

    @OneToMany(mappedBy = "candidateProfile", cascade = CascadeType.ALL)
    private List<Experience> experiences;

    @OneToMany(mappedBy = "candidateProfile", cascade = CascadeType.ALL)
    private List<Education> educations;

    @OneToMany(mappedBy = "candidateProfile", cascade = CascadeType.ALL)
    private List<Skill> skills;

    @OneToMany(mappedBy = "candidateProfile", cascade = CascadeType.ALL)
    private List<Application> applications;

    @OneToMany(mappedBy = "candidateProfile", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    private String cvUrl;
}
