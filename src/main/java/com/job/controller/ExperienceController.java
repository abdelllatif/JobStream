package com.job.controller;

import com.job.dto.request.ExperienceCreateRequestDTO;
import com.job.dto.request.ExperienceUpdateRequestDTO;
import com.job.dto.response.ExperienceResponseDTO;
import com.job.service.ExperienceService;
import com.job.service.CandidateProfileService;
import com.job.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/experiences")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;
    private final CandidateProfileService candidateProfileService;
    private final AuthUtil authUtil;

    @PostMapping
    public ExperienceResponseDTO create(@RequestBody ExperienceCreateRequestDTO dto) {
        Long userId = authUtil.getCurrentUserId();
        Long candidateProfileId = candidateProfileService.getByUserId(userId).getId();
        dto.setCandidateProfileId(candidateProfileId);
        return experienceService.create(dto);
    }

    @GetMapping("/{id}")
    public ExperienceResponseDTO getById(@PathVariable Long id) {
        return experienceService.getById(id);
    }

    @GetMapping
    public List<ExperienceResponseDTO> getAll() {
        return experienceService.getAll();
    }

    @GetMapping("/profile/{candidateProfileId}")
    public List<ExperienceResponseDTO> getByProfile(@PathVariable Long candidateProfileId) {
        return experienceService.getExperiencesByProfile(candidateProfileId);
    }

    @PutMapping("/{id}")
    public ExperienceResponseDTO update(@PathVariable Long id, @RequestBody ExperienceUpdateRequestDTO dto) {
        return experienceService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        experienceService.delete(id);
    }
}
