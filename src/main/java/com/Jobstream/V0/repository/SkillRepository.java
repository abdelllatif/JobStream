package com.Jobstream.V0.repository;

import com.Jobstream.V0.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {

    List<Skill> findByUserId(UUID userId);

    void deleteAllByUserId(UUID userId);
}
