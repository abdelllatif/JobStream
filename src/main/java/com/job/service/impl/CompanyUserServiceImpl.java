package com.job.service.impl;

import com.job.dto.request.CompanyUserCreateRequestDTO;
import com.job.dto.request.CompanyUserUpdateRequestDTO;
import com.job.dto.response.CompanyUserResponseDTO;
import com.job.entity.Company;
import com.job.entity.CompanyUser;
import com.job.entity.User;
import com.job.enums.CompanyRole;
import com.job.enums.MembershipStatus;
import com.job.exception.CompanyNotFoundException;
import com.job.exception.CompanyUserNotFoundException;
import com.job.exception.DuplicateCompanyUserException;
import com.job.exception.UserNotFoundException;
import com.job.mapper.CompanyUserMapper;
import com.job.repository.CompanyRepository;
import com.job.repository.CompanyUserRepository;
import com.job.repository.UserRepository;
import com.job.service.CompanyUserService;
import com.job.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyUserServiceImpl implements CompanyUserService {

    private final CompanyUserRepository companyUserRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CompanyUserMapper companyUserMapper;
    private final AuthUtil authUtil;

    @Override
    @Transactional
    public CompanyUserResponseDTO joinCompany(CompanyUserCreateRequestDTO dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with id: " + dto.getCompanyId()));

        // Check if user is already a member of this company
        if (companyUserRepository.existsByUserAndCompany(user, company)) {
            throw new DuplicateCompanyUserException("User is already a member of this company");
        }

        CompanyUser companyUser = companyUserMapper.toEntity(dto);
        companyUser.setUser(user);
        companyUser.setCompany(company);
        companyUser.setJoinedAt(LocalDate.now());
        companyUser.setStatus(MembershipStatus.PENDING);
        // Default role for joining requests - can be changed by Admin
        companyUser.setRole(CompanyRole.RECRUITER); 
        
        return companyUserMapper.toResponse(companyUserRepository.save(companyUser));
    }

    @Override
    public List<CompanyUserResponseDTO> getCompanyUsers(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with id: " + companyId));

        List<CompanyUser> companyUsers = companyUserRepository.findByCompany(company);
        return companyUsers.stream()
                .map(companyUserMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CompanyUserResponseDTO> getUserCompanies(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        List<CompanyUser> userCompanies = companyUserRepository.findByUser(user);
        return userCompanies.stream()
                .map(companyUserMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompanyUserResponseDTO updateCompanyUser(Long id, CompanyUserUpdateRequestDTO dto) {
        CompanyUser targetMember = companyUserRepository.findById(id)
                .orElseThrow(() -> new CompanyUserNotFoundException("CompanyUser not found with id: " + id));

        // Security check: Only CEO or HR of the company can update memberships
        User currentUser = authUtil.getCurrentUser();
        if (currentUser == null) throw new RuntimeException("User must be authenticated");

        CompanyUser requesterMembership = companyUserRepository.findByUserAndCompany(currentUser, targetMember.getCompany())
                .orElseThrow(() -> new RuntimeException("You are not a member of this company"));

        if (requesterMembership.getStatus() != MembershipStatus.ACTIVE || 
            (requesterMembership.getRole() != CompanyRole.CEO && requesterMembership.getRole() != CompanyRole.HR)) {
            throw new RuntimeException("Only ACTIVE CEO or HR can update membership statuses and roles");
        }

        if (dto.getJobTitle() != null) {
            targetMember.setJobTitle(dto.getJobTitle());
        }
        if (dto.getStatus() != null) {
            targetMember.setStatus(dto.getStatus());
        }
        if (dto.getCompanyRole() != null) {
            targetMember.setRole(dto.getCompanyRole());
        }

        return companyUserMapper.toResponse(companyUserRepository.save(targetMember));
    }

    @Override
    @Transactional
    public void leaveCompany(Long companyId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with id: " + companyId));

        CompanyUser companyUser = companyUserRepository.findByUserAndCompany(user, company)
                .orElseThrow(() -> new CompanyUserNotFoundException("User is not a member of this company"));

        companyUserRepository.delete(companyUser);
    }

    @Override
    public CompanyUserResponseDTO getById(Long id) {
        CompanyUser companyUser = companyUserRepository.findById(id)
                .orElseThrow(() -> new CompanyUserNotFoundException("CompanyUser not found with id: " + id));
        return companyUserMapper.toResponse(companyUser);
    }
}

