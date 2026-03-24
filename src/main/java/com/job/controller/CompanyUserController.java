package com.job.controller;

import com.job.dto.request.CompanyUserCreateRequestDTO;
import com.job.dto.request.CompanyUserUpdateRequestDTO;
import com.job.dto.response.CompanyUserResponseDTO;
import com.job.service.CompanyUserService;
import com.job.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company-users")
@RequiredArgsConstructor
public class CompanyUserController {

    private final CompanyUserService companyUserService;
    private final AuthUtil authUtil;

    @PostMapping("/join")
    public CompanyUserResponseDTO joinCompany(@RequestBody CompanyUserCreateRequestDTO dto) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("User not authenticated");
        }
        return companyUserService.joinCompany(dto, currentUserId);
    }

    @GetMapping("/company/{companyId}")
    public List<CompanyUserResponseDTO> getCompanyUsers(@PathVariable Long companyId) {
        return companyUserService.getCompanyUsers(companyId);
    }

    @GetMapping("/user/{userId}")
    public List<CompanyUserResponseDTO> getUserCompanies(@PathVariable Long userId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new RuntimeException("Access denied: You can only view your own companies");
        }
        return companyUserService.getUserCompanies(userId);
    }

    @GetMapping("/my-companies")
    public List<CompanyUserResponseDTO> getMyCompanies() {
        Long currentUserId = authUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("User not authenticated");
        }
        return companyUserService.getUserCompanies(currentUserId);
    }

    @GetMapping("/{id}")
    public CompanyUserResponseDTO getById(@PathVariable Long id) {
        return companyUserService.getById(id);
    }

    @PutMapping("/{id}")
    public CompanyUserResponseDTO updateCompanyUser(@PathVariable Long id, @RequestBody CompanyUserUpdateRequestDTO dto) {
        return companyUserService.updateCompanyUser(id, dto);
    }

    @DeleteMapping("/company/{companyId}")
    public void leaveCompany(@PathVariable Long companyId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new RuntimeException("User not authenticated");
        }
        companyUserService.leaveCompany(companyId, currentUserId);
    }
}

