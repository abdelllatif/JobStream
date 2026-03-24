package com.job.repository;

import com.job.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    List<Experience> findByCandidateProfileId(Long candidateProfileId);
}
