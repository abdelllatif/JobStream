package com.job.controller;

import com.job.dto.request.ExperienceCreateRequestDTO;
import com.job.dto.request.ExperienceUpdateRequestDTO;
import com.job.dto.response.ExperienceResponseDTO;
import com.job.service.ExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/experiences")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    @PostMapping
    public ExperienceResponseDTO create(@RequestBody ExperienceCreateRequestDTO dto) {
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

    @PutMapping("/{id}")
    public ExperienceResponseDTO update(@PathVariable Long id, @RequestBody ExperienceUpdateRequestDTO dto) {
        return experienceService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        experienceService.delete(id);
    }
}


