package com.job.repository;

import com.job.entity.Company;
import com.job.entity.CompanyUser;
import com.job.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyUserRepository extends JpaRepository<CompanyUser, Long> {
    List<CompanyUser> findByCompany(Company company);
    List<CompanyUser> findByUser(User user);
    Optional<CompanyUser> findByUserAndCompany(User user, Company company);
    boolean existsByUserAndCompany(User user, Company company);
    List<CompanyUser> findByCompanyAndStatus(Company company, com.job.enums.MembershipStatus status);
}

