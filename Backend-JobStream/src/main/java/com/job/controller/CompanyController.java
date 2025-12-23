package com.job.controller;

import com.job.dto.request.CompanyCreateRequestDTO;
import com.job.dto.request.CompanyUpdateRequestDTO;
import com.job.dto.response.CompanyResponseDTO;
import com.job.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public CompanyResponseDTO create(@RequestBody CompanyCreateRequestDTO dto) {
        return companyService.create(dto);
    }

    @GetMapping("/{id}")
    public CompanyResponseDTO getById(@PathVariable Long id) {
        return companyService.getById(id);
    }

    @GetMapping
    public List<CompanyResponseDTO> getAll() {
        return companyService.getAll();
    }

    @PutMapping("/{id}")
    public CompanyResponseDTO update(@PathVariable Long id, @RequestBody CompanyUpdateRequestDTO dto) {
        return companyService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        companyService.delete(id);
    }
}


