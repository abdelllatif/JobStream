package com.job.controller;

import com.job.dto.request.ApplicationCreateRequestDTO;
import com.job.dto.request.ApplicationUpdateRequestDTO;
import com.job.dto.response.ApplicationResponseDTO;
import com.job.service.ApplicationService;
import com.job.service.CandidateProfileService;
import com.job.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final AuthUtil authUtil;
    private final CandidateProfileService candidateProfileService ;
    @PostMapping
    public ApplicationResponseDTO create(@RequestBody ApplicationCreateRequestDTO dto) {
        Long userId = authUtil.getCurrentUserId();
        Long candidatProfil=candidateProfileService.getByUserId(userId).getId();
        dto.setCandidateProfileId(candidatProfil);
        return applicationService.create(dto);
    }

    @GetMapping("/{id}")
    public ApplicationResponseDTO getById(@PathVariable Long id) {
        return applicationService.getById(id);
    }

    @GetMapping
    public List<ApplicationResponseDTO> getAll() {
        return applicationService.getAll();
    }

    @PutMapping("/{id}")
    public ApplicationResponseDTO update(@PathVariable Long id, @RequestBody ApplicationUpdateRequestDTO dto) {
        return applicationService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        applicationService.delete(id);
    }

    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public Long countByUser() {
        Long userId = authUtil.getCurrentUserId();
        return applicationService.countByUserId(userId);
    }

    @GetMapping("/check-applied")
    public boolean checkApplied(@RequestParam Long jobId) {
        Long userId = authUtil.getCurrentUserId();
        return applicationService.hasApplied(jobId, userId);
    }

    @GetMapping("/my-applications")
    public List<ApplicationResponseDTO> getByCurrentUser() {
        Long userId = authUtil.getCurrentUserId();
        return applicationService.getApplicationsByUserId(userId);
    }
}


