package com.job.service.impl;

import com.job.dto.request.ApplicationCreateRequestDTO;
import com.job.dto.request.ApplicationUpdateRequestDTO;
import com.job.dto.response.ApplicationResponseDTO;
import com.job.entity.*;
import com.job.enums.ApplicationStatus;
import com.job.enums.CompanyRole;
import com.job.enums.MembershipStatus;
import com.job.enums.Role;
import com.job.exception.ApplicationNotFoundException;
import com.job.exception.JobNotFoundException;
import com.job.mapper.ApplicationMapper;
import com.job.repository.ApplicationRepository;
import com.job.repository.CandidateProfileRepository;
import com.job.repository.CompanyUserRepository;
import com.job.repository.JobRepository;
import com.job.util.AuthUtil;
import com.job.websocket.NotificationBroadcaster;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private CandidateProfileRepository candidateProfileRepository;
    @Mock
    private CompanyUserRepository companyUserRepository;
    @Mock
    private JobRepository jobRepository;
    @Mock
    private ApplicationMapper applicationMapper;
    @Mock
    private NotificationBroadcaster notificationBroadcaster;
    @Mock
    private AuthUtil authUtil;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private User candidateUser;
    private CandidateProfile candidateProfile;
    private Job job;
    private Company company;

    @BeforeEach
    void setUp() {
        candidateUser = new User();
        candidateUser.setId(1L);
        candidateUser.setRole(Role.CANDIDATE);
        candidateUser.setPremiumUser(false);

        candidateProfile = new CandidateProfile();
        candidateProfile.setId(1L);
        candidateProfile.setUser(candidateUser);

        company = new Company();
        company.setId(1L);

        job = new Job();
        job.setId(1L);
        job.setActive(true);
        job.setCompany(company);
        job.setTitle("Software Engineer");
    }

    @Test
    void create_ShouldSucceed_WhenJobIsActiveAndLimitNotReached() {
        // Arrange
        ApplicationCreateRequestDTO dto = new ApplicationCreateRequestDTO();
        dto.setJobId(1L);
        dto.setCandidateProfileId(1L);

        when(candidateProfileRepository.findById(1L)).thenReturn(Optional.of(candidateProfile));
        when(applicationRepository.countByCandidateProfileId(1L)).thenReturn(0L);
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(applicationMapper.toEntity(dto)).thenReturn(new Application());
        when(applicationRepository.save(any(Application.class))).thenReturn(new Application());
        when(applicationMapper.toResponse(any(Application.class))).thenReturn(new ApplicationResponseDTO());

        // Act
        ApplicationResponseDTO result = applicationService.create(dto);

        // Assert
        assertNotNull(result);
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void create_ShouldThrowException_WhenJobIsInactive() {
        // Arrange
        job.setActive(false);
        ApplicationCreateRequestDTO dto = new ApplicationCreateRequestDTO();
        dto.setJobId(1L);
        dto.setCandidateProfileId(1L);

        when(candidateProfileRepository.findById(1L)).thenReturn(Optional.of(candidateProfile));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(applicationMapper.toEntity(dto)).thenReturn(new Application());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> applicationService.create(dto));
        assertEquals("Cannot apply to an inactive job", exception.getMessage());
    }

    @Test
    void create_ShouldThrowException_WhenBasicUserReachesLimit() {
        // Arrange
        ApplicationCreateRequestDTO dto = new ApplicationCreateRequestDTO();
        dto.setJobId(1L);
        dto.setCandidateProfileId(1L);

        when(candidateProfileRepository.findById(1L)).thenReturn(Optional.of(candidateProfile));
        when(applicationRepository.countByCandidateProfileId(1L)).thenReturn(5L);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> applicationService.create(dto));
        assertTrue(exception.getMessage().contains("Application limit reached"));
    }

    @Test
    void update_ShouldSucceed_WhenUserIsAuthorizedCompanyMember() {
        // Arrange
        Long appId = 1L;
        Application application = new Application();
        application.setJob(job);
        
        ApplicationUpdateRequestDTO dto = new ApplicationUpdateRequestDTO();
        dto.setStatus(ApplicationStatus.ACCEPTED);

        User companyUser = new User();
        companyUser.setId(2L);
        
        CompanyUser membership = new CompanyUser();
        membership.setRole(CompanyRole.RECRUITER);
        membership.setStatus(MembershipStatus.ACTIVE);

        when(applicationRepository.findById(appId)).thenReturn(Optional.of(application));
        when(authUtil.getCurrentUser()).thenReturn(companyUser);
        when(companyUserRepository.findByUserAndCompany(companyUser, company)).thenReturn(Optional.of(membership));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);
        when(applicationMapper.toResponse(any(Application.class))).thenReturn(new ApplicationResponseDTO());

        // Act
        ApplicationResponseDTO result = applicationService.update(appId, dto);

        // Assert
        assertNotNull(result);
        assertEquals(ApplicationStatus.ACCEPTED, application.getStatus());
    }

    @Test
    void delete_ShouldSucceed_WhenUserIsOwner() {
        // Arrange
        Long appId = 1L;
        Application application = new Application();
        application.setCandidateProfile(candidateProfile);

        when(applicationRepository.findById(appId)).thenReturn(Optional.of(application));
        when(authUtil.getCurrentUser()).thenReturn(candidateUser);

        // Act
        applicationService.delete(appId);

        // Assert
        verify(applicationRepository).delete(application);
    }

    @Test
    void delete_ShouldThrowException_WhenUserIsNotOwnerOrAdmin() {
        // Arrange
        Long appId = 1L;
        Application application = new Application();
        application.setCandidateProfile(candidateProfile);

        User otherUser = new User();
        otherUser.setId(99L);
        otherUser.setRole(Role.CANDIDATE);

        when(applicationRepository.findById(appId)).thenReturn(Optional.of(application));
        when(authUtil.getCurrentUser()).thenReturn(otherUser);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> applicationService.delete(appId));
        assertEquals("User not authorized to delete this application", exception.getMessage());
    }
}
