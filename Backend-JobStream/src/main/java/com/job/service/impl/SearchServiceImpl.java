package com.job.service.impl;

import com.job.entity.Job;
import com.job.entity.User;
import com.job.repository.JobRepository;
import com.job.repository.UserRepository;
import com.job.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Override
    public Page<Job> searchJobs(String keyword, String location, String contractType, 
                               List<Long> domainIds, Pageable pageable) {
        // For now, implement basic search with JPA
        // In production, this would use Elasticsearch
        if (keyword != null && !keyword.trim().isEmpty()) {
            return jobRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    keyword, keyword, pageable);
        }
        return jobRepository.findAll(pageable);
    }

    @Override
    public List<Job> getRecommendedJobs(Long userId, int limit) {
        // Simple recommendation based on user's profile
        // In production, this would use ML algorithms
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ArrayList<>();
        }

        Pageable pageable = PageRequest.of(0, limit, Sort.by("postedAt").descending());
        return jobRepository.findByActiveTrueOrderByPostedAtDesc(pageable).getContent();
    }

    @Override
    public List<User> searchCandidates(String keyword, String location, List<String> skills, Pageable pageable) {
        // Basic candidate search
        // In production, this would use Elasticsearch with complex filtering
        if (keyword != null && !keyword.trim().isEmpty()) {
            return userRepository.findByRole(com.job.enums.Role.CANDIDATE).stream()
                    .filter(user -> (user.getFirstName() + " " + user.getLastName()).toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return userRepository.findByRole(com.job.enums.Role.CANDIDATE);
    }

    @Override
    public List<User> searchRecruiters(String keyword, String company, Pageable pageable) {
        // Basic recruiter search
        if (keyword != null && !keyword.trim().isEmpty()) {
            return userRepository.findByRole(com.job.enums.Role.RECRUITER).stream()
                    .filter(user -> (user.getFirstName() + " " + user.getLastName()).toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return userRepository.findByRole(com.job.enums.Role.RECRUITER);
    }

    @Override
    public Page<Job> advancedJobSearch(JobSearchCriteria criteria, Pageable pageable) {
        // Advanced search implementation
        // In production, this would use Elasticsearch with complex queries
        Sort sort = Sort.by(Sort.Direction.fromString(criteria.getSortOrder()), criteria.getSortBy());
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        
        if (criteria.getKeyword() != null && !criteria.getKeyword().trim().isEmpty()) {
            return jobRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    criteria.getKeyword(), criteria.getKeyword(), sortedPageable);
        }
        
        return jobRepository.findAll(sortedPageable);
    }

    @Override
    public List<String> getSuggestions(String query, String type) {
        // Simple suggestions implementation
        // In production, this would use Elasticsearch suggest API
        List<String> suggestions = new ArrayList<>();
        
        switch (type.toLowerCase()) {
            case "skills":
                suggestions.add("Java");
                suggestions.add("Python");
                suggestions.add("JavaScript");
                suggestions.add("React");
                suggestions.add("Spring Boot");
                break;
            case "locations":
                suggestions.add("Paris");
                suggestions.add("Lyon");
                suggestions.add("Marseille");
                suggestions.add("Remote");
                break;
            case "companies":
                suggestions.add("Google");
                suggestions.add("Microsoft");
                suggestions.add("Amazon");
                suggestions.add("Apple");
                break;
        }
        
        return suggestions.stream()
                .filter(s -> s.toLowerCase().contains(query.toLowerCase()))
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public void indexJob(Job job) {
        // In production, this would index job in Elasticsearch
        log.info("Indexing job: {}", job.getTitle());
    }

    @Override
    public void indexJobById(Long jobId) {
        // In production, this would fetch and index job in Elasticsearch
        Job job = jobRepository.findById(jobId).orElse(null);
        if (job != null) {
            log.info("Indexing job by ID {}: {}", jobId, job.getTitle());
        } else {
            log.warn("Job not found for indexing: {}", jobId);
        }
    }

    @Override
    public void indexUser(User user) {
        // In production, this would index user in Elasticsearch
        log.info("Indexing user: {} {}", user.getFirstName(), user.getLastName());
    }

    @Override
    public void indexUserById(Long userId) {
        // In production, this would fetch and index user in Elasticsearch
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            log.info("Indexing user by ID {}: {} {}", userId, user.getFirstName(), user.getLastName());
        } else {
            log.warn("User not found for indexing: {}", userId);
        }
    }

    @Override
    public void deleteJobFromIndex(Long jobId) {
        // In production, this would delete the job from Elasticsearch
        log.info("Deleting job from index: {}", jobId);
    }

    @Override
    public void deleteUserFromIndex(Long userId) {
        // In production, this would delete the user from Elasticsearch
        log.info("Deleting user from index: {}", userId);
    }
}
