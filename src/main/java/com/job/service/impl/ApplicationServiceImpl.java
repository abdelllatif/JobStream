package com.job.service.impl;

import com.job.dto.request.ApplicationCreateRequestDTO;
import com.job.dto.request.ApplicationUpdateRequestDTO;
import com.job.dto.response.ApplicationResponseDTO;
import com.job.entity.*;
import com.job.enums.ApplicationStatus;
import com.job.enums.CompanyRole;
import com.job.enums.MembershipStatus;
import com.job.exception.ApplicationNotFoundException;
import com.job.exception.JobNotFoundException;
import com.job.mapper.ApplicationMapper;
import com.job.repository.ApplicationRepository;
import com.job.repository.CandidateProfileRepository;
import com.job.repository.CompanyUserRepository;
import com.job.repository.JobRepository;
import com.job.service.ApplicationService;
import com.job.util.AuthUtil;
import com.job.websocket.NotificationBroadcaster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final CompanyUserRepository companyUserRepository;
    private final JobRepository jobRepository;
    private final ApplicationMapper applicationMapper;
    private final NotificationBroadcaster notificationBroadcaster;
    private final AuthUtil authUtil;

    @Override
    @Transactional
    public ApplicationResponseDTO create(ApplicationCreateRequestDTO dto) {
        CandidateProfile candidateProfile = candidateProfileRepository.findById(dto.getCandidateProfileId())
                .orElseThrow(() -> new RuntimeException(
                        "Candidate profile not found with id: " + dto.getCandidateProfileId()));

        // Subscription check: Limit BASIC users to 5 applications
        if (!candidateProfile.getUser().isPremiumUser()) {
            long applicationCount = applicationRepository.countByCandidateProfileId(dto.getCandidateProfileId());
            if (applicationCount >= 5) {
                throw new RuntimeException(
                        "Application limit reached for BASIC plan. Please upgrade to PREMIUM to apply for more jobs.");
            }
        }

        Application application = applicationMapper.toEntity(dto);

        Job job = jobRepository.findById(dto.getJobId())
                .orElseThrow(() -> new JobNotFoundException("Job not found with id: " + dto.getJobId()));

        if (!job.isActive()) {
            throw new RuntimeException("Cannot apply to an inactive job");
        }

        application.setCandidateProfile(candidateProfile);
        application.setJob(job);
        application.setStatus(ApplicationStatus.PENDING);
        application.setAppliedAt(LocalDateTime.now());

        Application saved = applicationRepository.save(application);

        // Notify job owner (recruiter) about new application
        if (job.getCompany() != null && job.getCompany().getOwner() != null) {
            Long recruiterUserId = job.getCompany().getOwner().getId();
            notificationBroadcaster.broadcastNotificationWithJob(
                    recruiterUserId,
                    "Nouvelle candidature",
                    "Vous avez reçu une nouvelle candidature pour le poste \"" + job.getTitle() + "\"",
                    com.job.enums.NotificationType.APPLICATION_RECEIVED,
                    job.getId());
        }

        return applicationMapper.toResponse(saved);
    }

    @Override
    public ApplicationResponseDTO getById(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + id));
        return applicationMapper.toResponse(application);
    }

    @Override
    public List<ApplicationResponseDTO> getAll() {
        return applicationRepository.findAll().stream()
                .map(applicationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApplicationResponseDTO update(Long id, ApplicationUpdateRequestDTO dto) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + id));

        // Security check: Only CEO, HR, or RECRUITER of the company can update status
        User currentUser = authUtil.getCurrentUser();
        if (currentUser == null) throw new RuntimeException("User must be authenticated");

        Company company = application.getJob().getCompany();
        CompanyUser membership = companyUserRepository.findByUserAndCompany(currentUser, company)
                .orElseThrow(() -> new RuntimeException("User is not a member of the company for this application"));

        if (membership.getStatus() != MembershipStatus.ACTIVE ||
            (membership.getRole() != CompanyRole.CEO && membership.getRole() != CompanyRole.HR && membership.getRole() != CompanyRole.RECRUITER)) {
            throw new RuntimeException("User does not have permission to update application status");
        }

        if (dto.getStatus() != null) {
            application.setStatus(dto.getStatus());
        }

        Application saved = applicationRepository.save(application);

        // Notify candidate about status change
        if (dto.getStatus() != null && application.getCandidateProfile() != null
                && application.getCandidateProfile().getUser() != null) {
            Long candidateUserId = application.getCandidateProfile().getUser().getId();
            String message = "Le statut de votre candidature pour le poste \""
                    + (application.getJob() != null ? application.getJob().getTitle() : "")
                    + "\" est passé à " + dto.getStatus().name();
            notificationBroadcaster.broadcastNotificationWithJob(
                    candidateUserId,
                    "Mise à jour de votre candidature",
                    message,
                    com.job.enums.NotificationType.APPLICATION_STATUS_CHANGED,
                    application.getJob() != null ? application.getJob().getId() : null);
        }

        return applicationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Application not found with id: " + id));
        
        // Security check: Only the candidate who applied or an ADMIN can delete/withdraw
        User currentUser = authUtil.getCurrentUser();
        if (currentUser == null) throw new RuntimeException("User must be authenticated");

        boolean isOwner = application.getCandidateProfile().getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == com.job.enums.Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("User not authorized to delete this application");
        }

        applicationRepository.delete(application);
    }

    @Override
    public List<ApplicationResponseDTO> getApplicationsByUserId(Long userId) {
        return applicationRepository.findByCandidateProfileUserId(userId).stream()
                .map(applicationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Long countByUserId(Long userId) {
        return candidateProfileRepository.findByUserId(userId)
                .map(profile -> applicationRepository.countByCandidateProfileId(profile.getId()))
                .orElse(0L);
    }

    @Override
    public boolean hasApplied(Long jobId, Long userId) {
        return applicationRepository.existsByJobIdAndCandidateProfileUserId(jobId, userId);
    }
}
