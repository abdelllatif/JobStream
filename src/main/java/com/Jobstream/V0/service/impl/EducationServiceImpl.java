package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.request.EducationRequest;
import com.Jobstream.V0.dto.response.EducationResponse;
import com.Jobstream.V0.entity.Education;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.exception.UnauthorizedException;
import com.Jobstream.V0.mapper.EducationMapper;
import com.Jobstream.V0.repository.EducationRepository;
import com.Jobstream.V0.repository.UserRepository;
import com.Jobstream.V0.service.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EducationResponse> getByUserId(UUID userId) {
        return educationRepository.findByUserIdOrderByStartDateDesc(userId)
                .stream().map(EducationMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EducationResponse create(UUID userId, EducationRequest request) {
        User user = findUser(userId);
        Education education = Education.builder()
                .user(user)
                .school(request.getSchool())
                .degree(request.getDegree())
                .fieldOfStudy(request.getFieldOfStudy())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .description(request.getDescription())
                .build();
        return EducationMapper.toResponse(educationRepository.save(education));
    }

    @Override
    @Transactional
    public EducationResponse update(UUID educationId, UUID userId, EducationRequest request) {
        Education education = findOwnedEducation(educationId, userId);
        education.setSchool(request.getSchool());
        education.setDegree(request.getDegree());
        education.setFieldOfStudy(request.getFieldOfStudy());
        education.setStartDate(request.getStartDate());
        education.setEndDate(request.getEndDate());
        education.setDescription(request.getDescription());
        return EducationMapper.toResponse(educationRepository.save(education));
    }

    @Override
    @Transactional
    public void delete(UUID educationId, UUID userId) {
        Education education = findOwnedEducation(educationId, userId);
        educationRepository.delete(education);
    }

    private Education findOwnedEducation(UUID educationId, UUID userId) {
        return educationRepository.findByIdAndUserId(educationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Education", "id", educationId));
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}
