package com.job.repository;

import com.job.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    
    Page<Job> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description, Pageable pageable);
    
    Page<Job> findByActiveTrueOrderByPostedAtDesc(Pageable pageable);
    
    List<Job> findByCompanyId(Long companyId);
    
    List<Job> findByDomainId(Long domainId);
    
    @Query("SELECT j FROM Job j WHERE j.location LIKE %:location%")
    List<Job> findByLocationContaining(@Param("location") String location);
    
    @Query("SELECT j FROM Job j WHERE j.contractType = :contractType")
    List<Job> findByContractType(@Param("contractType") String contractType);
    
    @Query("SELECT j FROM Job j WHERE j.active = true AND j.company.id = :companyId")
    List<Job> findActiveJobsByCompany(@Param("companyId") Long companyId);
    
    @Query("SELECT COUNT(j) FROM Job j WHERE j.active = true")
    long countActiveJobs();
    
    @Query("SELECT j FROM Job j WHERE j.title LIKE %:keyword% OR j.description LIKE %:keyword% ORDER BY j.postedAt DESC")
    List<Job> searchJobs(@Param("keyword") String keyword);
}

