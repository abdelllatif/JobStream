package com.Jobstream.V0.repository;

import com.Jobstream.V0.entity.CompanyUser;
import com.Jobstream.V0.enums.CompanyRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyUserRepository extends JpaRepository<CompanyUser, UUID> {

    List<CompanyUser> findByCompanyId(UUID companyId);

    Optional<CompanyUser> findByCompanyIdAndUserId(UUID companyId, UUID userId);

    boolean existsByCompanyIdAndUserId(UUID companyId, UUID userId);

    List<CompanyUser> findByCompanyIdAndRole(UUID companyId, CompanyRole role);

    void deleteByCompanyIdAndUserId(UUID companyId, UUID userId);
}
