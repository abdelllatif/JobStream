package com.job.controller;

import com.job.entity.ProfileVisit;
import com.job.service.ProfileVisitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile-visits")
@RequiredArgsConstructor
@Slf4j
public class ProfileVisitController {

    private final ProfileVisitService profileVisitService;

    @PostMapping("/record")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<ProfileVisit> recordVisit(
            @RequestParam Long visitorId,
            @RequestParam Long profileOwnerId) {
        try {
            ProfileVisit visit = profileVisitService.recordVisit(visitorId, profileOwnerId);
            if (visit != null) {
                return ResponseEntity.ok(visit);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Error recording visit from {} to {}: {}", visitorId, profileOwnerId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/profile/{profileOwnerId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<ProfileVisit>> getProfileVisits(@PathVariable Long profileOwnerId) {
        List<ProfileVisit> visits = profileVisitService.getProfileVisits(profileOwnerId);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/profile/{profileOwnerId}/range")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<ProfileVisit>> getProfileVisitsByDateRange(
            @PathVariable Long profileOwnerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<ProfileVisit> visits = profileVisitService.getProfileVisitsByDateRange(profileOwnerId, startDate, endDate);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/profile/{profileOwnerId}/count")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Map<String, Long>> getProfileVisitCount(@PathVariable Long profileOwnerId) {
        long count = profileVisitService.getProfileVisitCount(profileOwnerId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/profile/{profileOwnerId}/recent")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<ProfileVisit>> getRecentVisits(
            @PathVariable Long profileOwnerId,
            @RequestParam(defaultValue = "10") int limit) {
        List<ProfileVisit> visits = profileVisitService.getRecentVisits(profileOwnerId, limit);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/check")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Map<String, Boolean>> hasVisitedBefore(
            @RequestParam Long visitorId,
            @RequestParam Long profileOwnerId) {
        boolean hasVisited = profileVisitService.hasVisitedBefore(visitorId, profileOwnerId);
        return ResponseEntity.ok(Map.of("hasVisited", hasVisited));
    }
}
