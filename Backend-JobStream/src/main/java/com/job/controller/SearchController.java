package com.job.controller;

import com.job.entity.Job;
import com.job.entity.User;
import com.job.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/jobs")
    public ResponseEntity<Page<Job>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String contractType,
            @RequestParam(required = false) List<Long> domainIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Job> jobs = searchService.searchJobs(keyword, location, contractType, domainIds, pageable);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            log.error("Error searching jobs: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/jobs/advanced")
    public ResponseEntity<Page<Job>> advancedJobSearch(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String contractType,
            @RequestParam(required = false) List<Long> domainIds,
            @RequestParam(required = false) List<Long> tagIds,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(required = false) Boolean remote,
            @RequestParam(defaultValue = "postedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            SearchService.JobSearchCriteria criteria = new SearchService.JobSearchCriteria();
            criteria.setKeyword(keyword);
            criteria.setLocation(location);
            criteria.setContractType(contractType);
            criteria.setDomainIds(domainIds);
            criteria.setTagIds(tagIds);
            criteria.setMinSalary(minSalary);
            criteria.setMaxSalary(maxSalary);
            criteria.setExperienceLevel(experienceLevel);
            criteria.setRemote(remote);
            criteria.setSortBy(sortBy);
            criteria.setSortOrder(sortOrder);

            Pageable pageable = PageRequest.of(page, size);
            Page<Job> jobs = searchService.advancedJobSearch(criteria, pageable);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            log.error("Error in advanced job search: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/jobs/recommended/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<Job>> getRecommendedJobs(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Job> jobs = searchService.getRecommendedJobs(userId, limit);
            return ResponseEntity.ok(jobs);
        } catch (Exception e) {
            log.error("Error getting recommended jobs for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/candidates")
    @PreAuthorize("hasAnyRole('RECRUITER', 'ADMIN')")
    public ResponseEntity<List<User>> searchCandidates(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) List<String> skills,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            List<User> candidates = searchService.searchCandidates(keyword, location, skills, pageable);
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            log.error("Error searching candidates: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/recruiters")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'ADMIN')")
    public ResponseEntity<List<User>> searchRecruiters(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String company,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            List<User> recruiters = searchService.searchRecruiters(keyword, company, pageable);
            return ResponseEntity.ok(recruiters);
        } catch (Exception e) {
            log.error("Error searching recruiters: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSuggestions(
            @RequestParam String query,
            @RequestParam String type) {
        try {
            List<String> suggestions = searchService.getSuggestions(query, type);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            log.error("Error getting suggestions for query '{}' and type '{}': {}", query, type, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/index/job/{jobId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> indexJob(@PathVariable Long jobId) {
        try {
            searchService.indexJobById(jobId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error indexing job {}: {}", jobId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/index/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> indexUser(@PathVariable Long userId) {
        try {
            searchService.indexUserById(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error indexing user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/index/job/{jobId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteJobFromIndex(@PathVariable Long jobId) {
        try {
            searchService.deleteJobFromIndex(jobId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting job {} from index: {}", jobId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/index/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserFromIndex(@PathVariable Long userId) {
        try {
            searchService.deleteUserFromIndex(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting user {} from index: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
