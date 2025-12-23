package com.job.controller;

import com.job.dto.request.EducationCreateRequestDTO;
import com.job.dto.request.EducationUpdateRequestDTO;
import com.job.dto.response.EducationResponseDTO;
import com.job.service.EducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/educations")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    @PostMapping
    public EducationResponseDTO create(@RequestBody EducationCreateRequestDTO dto) {
        return educationService.create(dto);
    }

    @GetMapping("/{id}")
    public EducationResponseDTO getById(@PathVariable Long id) {
        return educationService.getById(id);
    }

    @GetMapping
    public List<EducationResponseDTO> getAll() {
        return educationService.getAll();
    }

    @PutMapping("/{id}")
    public EducationResponseDTO update(@PathVariable Long id, @RequestBody EducationUpdateRequestDTO dto) {
        return educationService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        educationService.delete(id);
    }
}


