package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.request.ApplicationRequest;
import com.Jobstream.V0.dto.response.ApplicationResponse;
import com.Jobstream.V0.entity.Application;
import com.Jobstream.V0.entity.Job;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.enums.ApplicationStatus;
import com.Jobstream.V0.enums.JobStatus;
import com.Jobstream.V0.exception.DuplicateResourceException;
import com.Jobstream.V0.repository.ApplicationRepository;
import com.Jobstream.V0.repository.JobRepository;
import com.Jobstream.V0.repository.UserRepository;
import com.Jobstream.V0.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    @Test
    void applyForJob_Success() {
        UUID userId = UUID.randomUUID();
        ApplicationRequest request = new ApplicationRequest();
        request.setJobId(UUID.randomUUID());
        request.setCoverLetter("Hello");

        Job job = Job.builder().id(request.getJobId()).status(JobStatus.OPEN).build();
        User user = User.builder().id(userId).build();

        when(jobRepository.findById(request.getJobId())).thenReturn(Optional.of(job));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(applicationRepository.existsByJobIdAndUserId(request.getJobId(), userId)).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> {
            Application a = i.getArgument(0);
            a.setId(UUID.randomUUID());
            return a;
        });

        ApplicationResponse response = applicationService.apply(userId, request);

        assertNotNull(response);
        assertEquals(ApplicationStatus.PENDING, response.getStatus());
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void applyForJob_AlreadyApplied() {
        UUID userId = UUID.randomUUID();
        ApplicationRequest request = new ApplicationRequest();
        request.setJobId(UUID.randomUUID());

        Job job = Job.builder().id(request.getJobId()).status(JobStatus.OPEN).build();
        when(jobRepository.findById(request.getJobId())).thenReturn(Optional.of(job));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(applicationRepository.existsByJobIdAndUserId(request.getJobId(), userId)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> applicationService.apply(userId, request));
        verify(applicationRepository, never()).save(any(Application.class));
    }
}
