package com.job.controller;

import com.job.dto.request.ApplicationCreateRequestDTO;
import com.job.dto.request.ApplicationUpdateRequestDTO;
import com.job.dto.response.ApplicationResponseDTO;
import com.job.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public ApplicationResponseDTO create(@RequestBody ApplicationCreateRequestDTO dto) {
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
}


