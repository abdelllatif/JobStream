package com.job.controller;

import com.job.dto.request.SkillRequestDTO;
import com.job.dto.response.SkillResponseDTO;
import com.job.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/candidate-profiles/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<SkillResponseDTO> create(@Valid @RequestBody SkillRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.create(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<SkillResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(skillService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<List<SkillResponseDTO>> getAll() {
        return ResponseEntity.ok(skillService.getAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<SkillResponseDTO> update(@PathVariable Long id, @Valid @RequestBody SkillRequestDTO dto) {
        return ResponseEntity.ok(skillService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        skillService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
