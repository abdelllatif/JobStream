package com.job.service;

import com.job.entity.ProfileVisit;

import java.time.LocalDateTime;
import java.util.List;

public interface ProfileVisitService {
    ProfileVisit recordVisit(Long visitorId, Long profileOwnerId);
    List<ProfileVisit> getProfileVisits(Long profileOwnerId);
    List<ProfileVisit> getProfileVisitsByDateRange(Long profileOwnerId, LocalDateTime startDate, LocalDateTime endDate);
    long getProfileVisitCount(Long profileOwnerId);
    List<ProfileVisit> getRecentVisits(Long profileOwnerId, int limit);
    boolean hasVisitedBefore(Long visitorId, Long profileOwnerId);
}
