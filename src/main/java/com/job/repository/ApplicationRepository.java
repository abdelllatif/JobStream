package com.job.repository;

import com.job.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    long countByCandidateProfileId(Long candidateProfileId);
    boolean existsByJobIdAndCandidateProfileUserId(Long jobId, Long userId);
    java.util.List<Application> findByCandidateProfileUserId(Long userId);
}
