package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.request.ApplicationRequest;
import com.Jobstream.V0.dto.request.UpdateApplicationStatusRequest;
import com.Jobstream.V0.dto.response.ApplicationResponse;
import com.Jobstream.V0.dto.response.PageResponse;
import com.Jobstream.V0.entity.Application;
import com.Jobstream.V0.entity.Job;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.enums.ApplicationStatus;
import com.Jobstream.V0.enums.NotificationType;
import com.Jobstream.V0.exception.BadRequestException;
import com.Jobstream.V0.exception.DuplicateResourceException;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.exception.UnauthorizedException;
import com.Jobstream.V0.mapper.ApplicationMapper;
import com.Jobstream.V0.repository.ApplicationRepository;
import com.Jobstream.V0.repository.CompanyUserRepository;
import com.Jobstream.V0.repository.JobRepository;
import com.Jobstream.V0.repository.UserRepository;
import com.Jobstream.V0.service.ApplicationService;
import com.Jobstream.V0.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final CompanyUserRepository companyUserRepository;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public ApplicationResponse apply(UUID userId, ApplicationRequest request) {
        Job job = findJob(request.getJobId());
        User user = findUser(userId);

        if (applicationRepository.existsByJobIdAndUserId(request.getJobId(), userId)) {
            throw new DuplicateResourceException("You have already applied for this job");
        }

        if (job.getStatus().name().equals("CLOSED")) {
            throw new BadRequestException("Job is no longer accepting applications");
        }

        Application application = Application.builder()
                .job(job).user(user)
                .cvUrl(request.getCvUrl())
                .coverLetter(request.getCoverLetter())
                .status(ApplicationStatus.PENDING)
                .build();
        application = applicationRepository.save(application);

        notificationService.createNotification(
                job.getCreatedBy(), NotificationType.JOB_APPLICATION,
                application.getId(),
                user.getEmail() + " applied to " + job.getTitle()
        );

        return ApplicationMapper.toResponse(application);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> getMyApplications(UUID userId, Pageable pageable) {
        Page<Application> page = applicationRepository.findByUserId(userId, pageable);
        return buildPageResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsByJob(UUID jobId, UUID recruiterId) {
        Job job = findJob(jobId);
        if (!companyUserRepository.existsByCompanyIdAndUserId(job.getCompany().getId(), recruiterId)) {
            throw new UnauthorizedException("Not a company member");
        }
        return applicationRepository.findByJobId(jobId)
                .stream().map(ApplicationMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApplicationResponse updateStatus(UUID applicationId, UUID recruiterId,
                                             UpdateApplicationStatusRequest request) {
        Application application = findApplication(applicationId);
        if (!companyUserRepository.existsByCompanyIdAndUserId(
                application.getJob().getCompany().getId(), recruiterId)) {
            throw new UnauthorizedException("Not a company member");
        }

        application.setStatus(request.getStatus());
        application = applicationRepository.save(application);

        NotificationType type = request.getStatus() == ApplicationStatus.ACCEPTED
                ? NotificationType.APPLICATION_ACCEPTED : NotificationType.APPLICATION_REJECTED;
        String msg = "Your application for " + application.getJob().getTitle() +
                " was " + request.getStatus().name().toLowerCase();
        notificationService.createNotification(application.getUser(), type, application.getId(), msg);

        ApplicationResponse updatedResponse = ApplicationMapper.toResponse(application);
        // Broadcast real-time status change to the applicant
        messagingTemplate.convertAndSend(
                "/topic/applications/" + application.getUser().getId(), updatedResponse);

        return updatedResponse;
    }

    @Override
    @Transactional
    public void withdraw(UUID applicationId, UUID userId) {
        Application application = findApplication(applicationId);
        if (!application.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Not your application");
        }
        applicationRepository.delete(application);
    }

    private PageResponse<ApplicationResponse> buildPageResponse(Page<Application> page) {
        return PageResponse.<ApplicationResponse>builder()
                .content(page.getContent().stream().map(ApplicationMapper::toResponse).collect(Collectors.toList()))
                .page(page.getNumber()).size(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages())
                .last(page.isLast()).build();
    }

    private Job findJob(UUID id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", id));
    }

    private User findUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private Application findApplication(UUID id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", id));
    }
}
