package com.Jobstream.V0.repository;

import com.Jobstream.V0.entity.Application;
import com.Jobstream.V0.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    Page<Application> findByUserId(UUID userId, Pageable pageable);

    List<Application> findByJobId(UUID jobId);

    Optional<Application> findByJobIdAndUserId(UUID jobId, UUID userId);

    boolean existsByJobIdAndUserId(UUID jobId, UUID userId);

    long countByJobId(UUID jobId);

    long countByJobIdAndStatus(UUID jobId, ApplicationStatus status);

    @Query("SELECT a FROM Application a WHERE a.job.company.id = :companyId")
    Page<Application> findByCompanyId(@Param("companyId") UUID companyId, Pageable pageable);
}
