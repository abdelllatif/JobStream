package com.Jobstream.V0.repository;

import com.Jobstream.V0.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EducationRepository extends JpaRepository<Education, UUID> {

    List<Education> findByUserIdOrderByStartDateDesc(UUID userId);

    Optional<Education> findByIdAndUserId(UUID id, UUID userId);
}
