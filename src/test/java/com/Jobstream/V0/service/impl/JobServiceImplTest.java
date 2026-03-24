package com.Jobstream.V0.service.impl;

import com.Jobstream.V0.dto.request.JobRequest;
import com.Jobstream.V0.dto.response.JobResponse;
import com.Jobstream.V0.entity.Company;
import com.Jobstream.V0.entity.Job;
import com.Jobstream.V0.enums.JobStatus;
import com.Jobstream.V0.enums.JobType;
import com.Jobstream.V0.repository.CompanyRepository;
import com.Jobstream.V0.repository.JobRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceImplTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    @Test
    void createJob_Success() {
        UUID companyId = UUID.randomUUID();
        JobRequest request = new JobRequest();
        request.setCompanyId(companyId);
        request.setTitle("DevOps Engineer");
        request.setDescription("Exciting role");
        request.setJobType(JobType.FULL_TIME);

        Company company = Company.builder().id(companyId).build();
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(jobRepository.save(any(Job.class))).thenAnswer(i -> {
            Job j = i.getArgument(0);
            j.setId(UUID.randomUUID());
            return j;
        });

        JobResponse response = jobService.create(UUID.randomUUID(), request);

        assertNotNull(response);
        assertEquals("DevOps Engineer", response.getTitle());
        verify(jobRepository).save(any(Job.class));
    }

    @Test
    void getJobById_Success() {
        UUID jobId = UUID.randomUUID();
        Job job = Job.builder()
                .id(jobId)
                .title("Data Scientist")
                .company(new Company())
                .build();

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        JobResponse response = jobService.getById(jobId);

        assertNotNull(response);
        assertEquals("Data Scientist", response.getTitle());
    }
}
