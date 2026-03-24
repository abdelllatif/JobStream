package com.Jobstream.V0.controller;

import com.Jobstream.V0.dto.request.AddCompanyEmployeeRequest;
import com.Jobstream.V0.dto.request.CompanyRequest;
import com.Jobstream.V0.dto.response.CompanyResponse;
import com.Jobstream.V0.dto.response.CompanyUserResponse;
import com.Jobstream.V0.entity.User;
import com.Jobstream.V0.exception.ResourceNotFoundException;
import com.Jobstream.V0.repository.UserRepository;
import com.Jobstream.V0.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Companies", description = "Endpoints for company and employee management")
public class CompanyController {

    private final CompanyService companyService;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Register a new company")
    public ResponseEntity<CompanyResponse> createCompany(
            @Valid @RequestBody CompanyRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyService.create(getCurrentUserId(auth), request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update company details")
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable UUID id, @Valid @RequestBody CompanyRequest request, Authentication auth) {
        return ResponseEntity.ok(companyService.update(id, getCurrentUserId(auth), request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get company by ID")
    public ResponseEntity<CompanyResponse> getCompany(@PathVariable UUID id) {
        return ResponseEntity.ok(companyService.getById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search companies by name")
    public ResponseEntity<Page<CompanyResponse>> searchCompanies(
            @RequestParam String query, Pageable pageable) {
        return ResponseEntity.ok(companyService.search(query, pageable));
    }

    @GetMapping("/my")
    @Operation(summary = "Get companies current user belongs to")
    public ResponseEntity<List<CompanyResponse>> getMyCompanies(Authentication auth) {
        return ResponseEntity.ok(companyService.getMyCompanies(getCurrentUserId(auth)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a company")
    public ResponseEntity<Void> deleteCompany(@PathVariable UUID id, Authentication auth) {
        companyService.delete(id, getCurrentUserId(auth));
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload company logo")
    public ResponseEntity<CompanyResponse> uploadLogo(
            @PathVariable UUID id, @RequestParam("file") MultipartFile file, Authentication auth) {
        return ResponseEntity.ok(companyService.uploadLogo(id, getCurrentUserId(auth), file));
    }

    @PostMapping("/{id}/employees")
    @Operation(summary = "Add an employee to the company")
    public ResponseEntity<CompanyUserResponse> addEmployee(
            @PathVariable UUID id, @Valid @RequestBody AddCompanyEmployeeRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyService.addEmployee(id, getCurrentUserId(auth), request));
    }

    @DeleteMapping("/{companyId}/employees/{memberId}")
    @Operation(summary = "Remove an employee from the company")
    public ResponseEntity<Void> removeEmployee(
            @PathVariable UUID companyId, @PathVariable UUID memberId, Authentication auth) {
        companyService.removeEmployee(companyId, getCurrentUserId(auth), memberId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/employees")
    @Operation(summary = "Get all employees of a company")
    public ResponseEntity<List<CompanyUserResponse>> getEmployees(@PathVariable UUID id) {
        return ResponseEntity.ok(companyService.getEmployees(id));
    }

    private UUID getCurrentUserId(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getId();
    }
}
