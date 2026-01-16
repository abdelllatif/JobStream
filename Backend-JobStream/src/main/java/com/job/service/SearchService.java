package com.job.service;

import com.job.entity.Job;
import com.job.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchService {
    Page<Job> searchJobs(String keyword, String location, String contractType, 
                        List<Long> domainIds, Pageable pageable);
    List<Job> getRecommendedJobs(Long userId, int limit);
    List<User> searchCandidates(String keyword, String location, List<String> skills, Pageable pageable);
    List<User> searchRecruiters(String keyword, String company, Pageable pageable);
    Page<Job> advancedJobSearch(JobSearchCriteria criteria, Pageable pageable);
    List<String> getSuggestions(String query, String type);
    void indexJob(Job job);
    void indexJobById(Long jobId);
    void indexUser(User user);
    void indexUserById(Long userId);
    void deleteJobFromIndex(Long jobId);
    void deleteUserFromIndex(Long userId);
    
    class JobSearchCriteria {
        private String keyword;
        private String location;
        private String contractType;
        private List<Long> domainIds;
        private List<Long> tagIds;
        private Double minSalary;
        private Double maxSalary;
        private String experienceLevel;
        private Boolean remote;
        private String sortBy;
        private String sortOrder;
        
        // Getters and Setters
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getContractType() { return contractType; }
        public void setContractType(String contractType) { this.contractType = contractType; }
        public List<Long> getDomainIds() { return domainIds; }
        public void setDomainIds(List<Long> domainIds) { this.domainIds = domainIds; }
        public List<Long> getTagIds() { return tagIds; }
        public void setTagIds(List<Long> tagIds) { this.tagIds = tagIds; }
        public Double getMinSalary() { return minSalary; }
        public void setMinSalary(Double minSalary) { this.minSalary = minSalary; }
        public Double getMaxSalary() { return maxSalary; }
        public void setMaxSalary(Double maxSalary) { this.maxSalary = maxSalary; }
        public String getExperienceLevel() { return experienceLevel; }
        public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
        public Boolean getRemote() { return remote; }
        public void setRemote(Boolean remote) { this.remote = remote; }
        public String getSortBy() { return sortBy; }
        public void setSortBy(String sortBy) { this.sortBy = sortBy; }
        public String getSortOrder() { return sortOrder; }
        public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
    }
}
