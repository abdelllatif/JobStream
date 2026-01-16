package com.job.controller;

import com.job.dto.request.DomainCreateRequestDTO;
import com.job.dto.response.DomainResponseDTO;
import com.job.service.DomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/domains")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DomainController {

    private final DomainService domainService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
    public ResponseEntity<DomainResponseDTO> createDomain(
            @Valid @RequestBody DomainCreateRequestDTO dto) {
        DomainResponseDTO createdDomain = domainService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDomain);
    }

    @GetMapping
    public ResponseEntity<List<DomainResponseDTO>> getAllDomains() {
        List<DomainResponseDTO> domains = domainService.getAll();
        return ResponseEntity.ok(domains);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DomainResponseDTO> getDomainById(
            @PathVariable Long id) {
        return domainService.getById(id)
                .map(domain -> ResponseEntity.ok(domain))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<DomainResponseDTO> getDomainByName(
             @PathVariable String name) {
        return domainService.getByName(name)
                .map(domain -> ResponseEntity.ok(domain))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECRUITER')")
    public ResponseEntity<DomainResponseDTO> updateDomain(
            @PathVariable Long id,
            @Valid @RequestBody DomainCreateRequestDTO dto) {
        try {
            DomainResponseDTO updatedDomain = domainService.update(id, dto);
            return ResponseEntity.ok(updatedDomain);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDomain(
             @PathVariable Long id) {
        try {
            domainService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exists/{name}")
    public ResponseEntity<Boolean> checkDomainExists(
            @PathVariable String name) {
        boolean exists = domainService.existsByName(name);
        return ResponseEntity.ok(exists);
    }
}
