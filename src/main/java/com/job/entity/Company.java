package com.job.entity;


import jakarta.persistence.*;
import java.util.List;
import lombok.Data;

@Entity
@Data
@Table(name = "companies")
public class Company {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String website;
    private String logoUrl;
    
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    
    @ManyToOne
    @JoinColumn(name = "domain_id")
    private Domain domain;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Job> jobs;
}
