package com.job.repository;

import com.job.entity.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DomainRepository extends JpaRepository<Domain, Long> {
    
    Optional<Domain> findByName(String name);
    
    boolean existsByName(String name);
}

