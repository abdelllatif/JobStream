package com.job.controller;

import com.job.dto.request.CandidateProfileCreateRequestDTO;
import com.job.dto.request.CandidateProfileUpdateRequestDTO;
import com.job.dto.response.CandidateProfileResponseDTO;
import com.job.service.CandidateProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidate-profiles")
@RequiredArgsConstructor
public class CandidateProfileController {

    private final CandidateProfileService candidateProfileService;

    @PostMapping
    public CandidateProfileResponseDTO create(@RequestBody CandidateProfileCreateRequestDTO dto) {
        return candidateProfileService.create(dto);
    }

    @GetMapping("/{id}")
    public CandidateProfileResponseDTO getById(@PathVariable Long id) {
        return candidateProfileService.getById(id);
    }

    @GetMapping
    public List<CandidateProfileResponseDTO> getAll() {
        return candidateProfileService.getAll();
    }

    @PutMapping("/{id}")
    public CandidateProfileResponseDTO update(@PathVariable Long id, @RequestBody CandidateProfileUpdateRequestDTO dto) {
        return candidateProfileService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        candidateProfileService.delete(id);
    }
}


