package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.request.SkillRequest;
import com.Jobstream.V0.dto.response.SkillResponse;
import com.Jobstream.V0.entity.Skill;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.exception.UnauthorizedException;
import com.Jobstream.V0.mapper.SkillMapper;
import com.Jobstream.V0.repository.SkillRepository;
import com.Jobstream.V0.repository.UserRepository;
import com.Jobstream.V0.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> getByUserId(UUID userId) {
        return skillRepository.findByUserId(userId)
                .stream().map(SkillMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SkillResponse addSkill(UUID userId, SkillRequest request) {
        User user = findUser(userId);
        Skill skill = Skill.builder().user(user).name(request.getName()).build();
        return SkillMapper.toResponse(skillRepository.save(skill));
    }

    @Override
    @Transactional
    public void deleteSkill(UUID skillId, UUID userId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", skillId));
        if (!skill.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Not your skill");
        }
        skillRepository.delete(skill);
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}
