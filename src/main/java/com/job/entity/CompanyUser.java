package com.job.entity;

import com.job.enums.CompanyRole;
import com.job.enums.MembershipStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "company_users")
public class CompanyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    private String jobTitle;

    private LocalDate joinedAt;

    @Enumerated(EnumType.STRING)
    private CompanyRole role;

    @Enumerated(EnumType.STRING)
    private MembershipStatus status;
}

