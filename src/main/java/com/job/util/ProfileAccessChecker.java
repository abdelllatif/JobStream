package com.job.util;

import com.job.entity.CandidateProfile;
import com.job.exception.AccessDeniedException;
import com.job.service.CandidateProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileAccessChecker {

    private final AuthUtil authUtil;
    private final CandidateProfileService candidateProfileService;

    public boolean canAccessProfile(Long profileId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }

        Long currentUserId = authUtil.getCurrentUserId();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        CandidateProfile profile = candidateProfileService.getEntityById(profileId);
        if (profile == null) {
            throw new AccessDeniedException("This profile doesn't exist");
        }

        // Allow owner or Admin
        if (isAdmin || profile.getUser().getId().equals(currentUserId)) {
            return true;
        }

        throw new AccessDeniedException("You're not allowed to access this profile. Owner ID: " 
                + profile.getUser().getId() + ", Current User ID: " + currentUserId);
    }
}
