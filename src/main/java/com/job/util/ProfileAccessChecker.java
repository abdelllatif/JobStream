package com.job.util;

import com.job.entity.CandidateProfile;
import com.job.exception.AccessDeniedException;
import com.job.service.CandidateProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileAccessChecker {

    private final AuthUtil authUtil;
    private final CandidateProfileService candidateProfileService;

    public boolean canAccessProfile(Long profileId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        CandidateProfile profile = candidateProfileService.getEntityById(profileId);
        if (profile == null) {
            throw new AccessDeniedException("This profile doesn't exist");
        }

        if (!profile.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("You're not allowed to access this profile");
        }

        return true;
    }
}
