package com.job.entity;
import jakarta.persistence.*;
import java.util.List;
import lombok.Data;

@Entity
@Data
@Table(name = "tags")
public class Tag {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "tags")
    private List<Job> jobs;
}
