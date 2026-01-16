package com.job.entity;

import com.job.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String profilePicture;
    private String phone;
    private String bio;
    private String location;
    private String website;
    private String linkedinProfile;
    private boolean emailVerified;
    private boolean premiumUser;
    private String googleId;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private CandidateProfile candidateProfile;

    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL)
    private Company companyProfile;

    @OneToMany(mappedBy = "requester")
    private List<Connection> sentConnections;

    @OneToMany(mappedBy = "receiver")
    private List<Connection> receivedConnections;

    @OneToMany(mappedBy = "visitor")
    private List<ProfileVisit> profileVisits;

    @OneToMany(mappedBy = "profileOwner")
    private List<ProfileVisit> receivedVisits;

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications;

    @OneToMany(mappedBy = "sender")
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "receiver")
    private List<Message> receivedMessages;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private PremiumSubscription premiumSubscription;

    @OneToMany(mappedBy = "user")
    private List<Payment> payments;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
