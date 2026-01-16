package com.job.service.impl;

import com.job.entity.ProfileVisit;
import com.job.entity.User;
import com.job.repository.ProfileVisitRepository;
import com.job.repository.UserRepository;
import com.job.service.ProfileVisitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileVisitServiceImpl implements ProfileVisitService {

    private final ProfileVisitRepository profileVisitRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProfileVisit recordVisit(Long visitorId, Long profileOwnerId) {
        if (visitorId.equals(profileOwnerId)) {
            return null;
        }

        User visitor = userRepository.findById(visitorId)
                .orElseThrow(() -> new RuntimeException("Visitor not found"));
        User profileOwner = userRepository.findById(profileOwnerId)
                .orElseThrow(() -> new RuntimeException("Profile owner not found"));

        ProfileVisit visit = new ProfileVisit();
        visit.setVisitor(visitor);
        visit.setProfileOwner(profileOwner);
        visit.setVisitedAt(LocalDateTime.now());
        visit.setCreatedAt(LocalDateTime.now());

        ProfileVisit savedVisit = profileVisitRepository.save(visit);
        log.info("Recorded profile visit from user {} to user {}", visitorId, profileOwnerId);
        return savedVisit;
    }

    @Override
    public List<ProfileVisit> getProfileVisits(Long profileOwnerId) {
        return profileVisitRepository.findByProfileOwnerIdOrderByVisitedAtDesc(profileOwnerId);
    }

    @Override
    public List<ProfileVisit> getProfileVisitsByDateRange(Long profileOwnerId, LocalDateTime startDate, LocalDateTime endDate) {
        return profileVisitRepository.findByProfileOwnerIdAndVisitedAtBetweenOrderByVisitedAtDesc(
                profileOwnerId, startDate, endDate);
    }

    @Override
    public long getProfileVisitCount(Long profileOwnerId) {
        return profileVisitRepository.countByProfileOwnerId(profileOwnerId);
    }

    @Override
    public List<ProfileVisit> getRecentVisits(Long profileOwnerId, int limit) {
        return profileVisitRepository.findRecentVisits(profileOwnerId, limit);
    }

    @Override
    public boolean hasVisitedBefore(Long visitorId, Long profileOwnerId) {
        return profileVisitRepository.existsByVisitorIdAndProfileOwnerId(visitorId, profileOwnerId);
    }
}
