package com.job.entity;
import jakarta.persistence.*;
import java.util.List;
@Entity
@Table(name = "domains")
public class Domain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "domain")
    private List<Job> jobs;
}
