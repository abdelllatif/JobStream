package com.Jobstream.V0.repository;

import com.Jobstream.V0.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    List<Company> findByCreatedById(UUID userId);

    @Query("SELECT c FROM Company c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Company> searchByName(@Param("query") String query, Pageable pageable);

    @Query("SELECT cu.company FROM CompanyUser cu WHERE cu.user.id = :userId")
    List<Company> findCompaniesByUserId(@Param("userId") UUID userId);
}
