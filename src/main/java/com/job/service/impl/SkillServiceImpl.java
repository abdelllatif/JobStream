package com.job.service.impl;

import com.job.dto.request.SkillRequestDTO;
import com.job.dto.response.SkillResponseDTO;
import com.job.entity.CandidateProfile;
import com.job.entity.Skill;
import com.job.repository.SkillRepository;
import com.job.service.CandidateProfileService;
import com.job.service.SkillService;
import com.job.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final CandidateProfileService candidateProfileService;
    private final AuthUtil authUtil;

    @Override
    @Transactional
    public SkillResponseDTO create(SkillRequestDTO dto) {
        Long userId = authUtil.getCurrentUserId();
        CandidateProfile profile = candidateProfileService.getEntityByUserId(userId);

        Skill skill = new Skill();
        skill.setName(dto.getName());
        skill.setLevel(dto.getLevel());
        skill.setCandidateProfile(profile);

        return toResponseDTO(skillRepository.save(skill));
    }

    @Override
    public SkillResponseDTO getById(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));
        return toResponseDTO(skill);
    }

    @Override
    public List<SkillResponseDTO> getAll() {
        Long userId = authUtil.getCurrentUserId();
        CandidateProfile profile = candidateProfileService.getEntityByUserId(userId);

        return profile.getSkills().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SkillResponseDTO update(Long id, SkillRequestDTO dto) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        Long userId = authUtil.getCurrentUserId();
        if (!skill.getCandidateProfile().getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to update this skill");
        }

        skill.setName(dto.getName());
        skill.setLevel(dto.getLevel());

        return toResponseDTO(skillRepository.save(skill));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        Long userId = authUtil.getCurrentUserId();
        if (!skill.getCandidateProfile().getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this skill");
        }

        skillRepository.delete(skill);
    }

    private SkillResponseDTO toResponseDTO(Skill skill) {
        SkillResponseDTO dto = new SkillResponseDTO();
        dto.setId(skill.getId());
        dto.setName(skill.getName());
        dto.setLevel(skill.getLevel());
        return dto;
    }
}
