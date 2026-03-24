package com.job.controller;

import com.job.dto.request.CandidateProfileCreateRequestDTO;
import com.job.dto.request.CandidateProfileUpdateRequestDTO;
import com.job.dto.response.CandidateProfileResponseDTO;
import com.job.entity.CandidateProfile;
import com.job.service.CandidateProfileService;
import com.job.util.AuthUtil;
import com.job.util.ProfileAccessChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidate-profiles")
@RequiredArgsConstructor
public class CandidateProfileController {

    private final CandidateProfileService candidateProfileService;
    private final AuthUtil authUtil;
    private final ProfileAccessChecker profileAccessChecker;
    @PostMapping
    public CandidateProfileResponseDTO create(@RequestBody CandidateProfileCreateRequestDTO dto) {

        Long userId = authUtil.getCurrentUserId();
        dto.setUserId(userId);
        return candidateProfileService.create(dto);
    }

    @GetMapping("/{id}")
    public CandidateProfileResponseDTO getById(@PathVariable Long id) {

        profileAccessChecker.canAccessProfile(id);
        return candidateProfileService.getById(id);
    }

    @GetMapping("/user/{userId}")
    public CandidateProfileResponseDTO getByUserId(@PathVariable Long userId) {
        return candidateProfileService.getByUserId(userId);
    }

    @GetMapping
    public List<CandidateProfileResponseDTO> getAll() {
        return candidateProfileService.getAll();
    }

    @GetMapping("/me")
    public CandidateProfileResponseDTO getMyProfile() {
        Long userId = authUtil.getCurrentUserId();
        return candidateProfileService.getByUserId(userId);
    }

    @PutMapping("/me")
    public CandidateProfileResponseDTO updateMyProfile(@RequestBody CandidateProfileUpdateRequestDTO dto) {
        Long userId = authUtil.getCurrentUserId();
        CandidateProfile profile = candidateProfileService.getEntityByUserId(userId);
        return candidateProfileService.update(profile.getId(), dto);
    }

    @PutMapping("/{id}")
    public CandidateProfileResponseDTO update(@PathVariable Long id, @RequestBody CandidateProfileUpdateRequestDTO dto) {
        profileAccessChecker.canAccessProfile(id);
        return candidateProfileService.update(id, dto);
    }



    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        profileAccessChecker.canAccessProfile(id);
        candidateProfileService.delete(id);
    }
}


