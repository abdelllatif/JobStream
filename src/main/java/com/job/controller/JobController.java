package com.job.controller;

import com.job.dto.request.JobCreateRequestDTO;
import com.job.dto.request.JobUpdateRequestDTO;
import com.job.dto.response.JobResponseDTO;
import com.job.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public JobResponseDTO create(@RequestBody JobCreateRequestDTO dto) {
        return jobService.create(dto);
    }

    @GetMapping("/{id}")
    public JobResponseDTO getById(@PathVariable Long id) {
        return jobService.getById(id);
    }

    @GetMapping
    public List<JobResponseDTO> getAll() {
        return jobService.getAll();
    }

    @PutMapping("/{id}")
    public JobResponseDTO update(@PathVariable Long id, @RequestBody JobUpdateRequestDTO dto) {
        return jobService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        jobService.delete(id);
    }
}


