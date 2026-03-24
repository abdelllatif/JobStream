package com.job.service.impl;

import com.job.dto.request.JobCreateRequestDTO;
import com.job.dto.request.JobUpdateRequestDTO;
import com.job.dto.response.JobResponseDTO;
import com.job.entity.*;
import com.job.enums.CompanyRole;
import com.job.enums.MembershipStatus;
import com.job.exception.CompanyNotFoundException;
import com.job.exception.JobNotFoundException;
import com.job.mapper.JobMapper;
import com.job.repository.CompanyRepository;
import com.job.repository.CompanyUserRepository;
import com.job.repository.DomainRepository;
import com.job.repository.JobRepository;
import com.job.util.AuthUtil;
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
class JobServiceImplTest {

    @Mock
    private JobRepository jobRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private CompanyUserRepository companyUserRepository;
    @Mock
    private DomainRepository domainRepository;
    @Mock
    private JobMapper jobMapper;
    @Mock
    private AuthUtil authUtil;

    @InjectMocks
    private JobServiceImpl jobService;

    private User currentUser;
    private Company company;
    private Domain domain;
    private CompanyUser membership;

    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);

        company = new Company();
        company.setId(10L);
        company.setName("Test Company");

        domain = new Domain();
        domain.setId(5L);
        domain.setName("IT");

        membership = new CompanyUser();
        membership.setUser(currentUser);
        membership.setCompany(company);
        membership.setStatus(MembershipStatus.ACTIVE);
        membership.setRole(CompanyRole.CEO);
    }

    @Test
    void create_ShouldSucceed_WhenUserIsAuthorized() {
        // Arrange
        JobCreateRequestDTO dto = new JobCreateRequestDTO();
        dto.setCompanyId(10L);
        dto.setDomainId(5L);
        dto.setDescription("Test Job #java #spring");

        Job job = new Job();
        job.setCompany(company);

        when(authUtil.getCurrentUser()).thenReturn(currentUser);
        when(companyRepository.findById(10L)).thenReturn(Optional.of(company));
        when(companyUserRepository.findByUserAndCompany(currentUser, company)).thenReturn(Optional.of(membership));
        when(domainRepository.findById(5L)).thenReturn(Optional.of(domain));
        when(jobMapper.toEntity(dto)).thenReturn(job);
        when(jobRepository.save(any(Job.class))).thenReturn(job);
        when(jobMapper.toResponse(any(Job.class))).thenReturn(new JobResponseDTO());

        // Act
        JobResponseDTO result = jobService.create(dto);

        // Assert
        assertNotNull(result);
        verify(jobRepository).save(any(Job.class));
    }

    @Test
    void create_ShouldThrowException_WhenUserNotMember() {
        // Arrange
        JobCreateRequestDTO dto = new JobCreateRequestDTO();
        dto.setCompanyId(10L);

        when(authUtil.getCurrentUser()).thenReturn(currentUser);
        when(companyRepository.findById(10L)).thenReturn(Optional.of(company));
        when(companyUserRepository.findByUserAndCompany(currentUser, company)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> jobService.create(dto));
        assertEquals("User is not a member of this company", exception.getMessage());
    }

    @Test
    void create_ShouldThrowException_WhenUserHasInsufficientRole() {
        // Arrange
        membership.setRole(CompanyRole.Employer); // Use correct enum value
        JobCreateRequestDTO dto = new JobCreateRequestDTO();
        dto.setCompanyId(10L);

        when(authUtil.getCurrentUser()).thenReturn(currentUser);
        when(companyRepository.findById(10L)).thenReturn(Optional.of(company));
        when(companyUserRepository.findByUserAndCompany(currentUser, company)).thenReturn(Optional.of(membership));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> jobService.create(dto));
        assertTrue(exception.getMessage().contains("User does not have permission"));
    }

    @Test
    void delete_ShouldSucceed_WhenAuthorized() {
        // Arrange
        Long jobId = 1L;
        Job job = new Job();
        job.setId(jobId);
        job.setCompany(company);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(authUtil.getCurrentUser()).thenReturn(currentUser);
        when(companyRepository.findById(10L)).thenReturn(Optional.of(company));
        when(companyUserRepository.findByUserAndCompany(currentUser, company)).thenReturn(Optional.of(membership));

        // Act
        jobService.delete(jobId);

        // Assert
        verify(jobRepository).delete(job);
    }

    @Test
    void delete_ShouldThrowJobNotFound_WhenJobDoesNotExist() {
        // Arrange
        when(jobRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(JobNotFoundException.class, () -> jobService.delete(1L));
    }
}
