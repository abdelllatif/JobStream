package com.job.controller;

import com.job.entity.Payment;
import com.job.enums.Role;
import com.job.repository.ApplicationRepository;
import com.job.repository.JobRepository;
import com.job.repository.PaymentRepository;
import com.job.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    @GetMapping("/jobs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getJobStats() {
        long totalJobs = jobRepository.count();
        return ResponseEntity.ok(Map.of("totalJobs", totalJobs));
    }

    @GetMapping("/applications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getApplicationStats() {
        long totalApplications = applicationRepository.count();
        return ResponseEntity.ok(Map.of("totalApplications", totalApplications));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getUserStats() {
        long totalUsers = userRepository.count();
        long candidates = userRepository.countByRole(Role.CANDIDATE);
        long recruiters = userRepository.countByRole(Role.RECRUITER);
        long admins = userRepository.countByRole(Role.ADMIN);
        return ResponseEntity.ok(Map.of(
                "totalUsers", totalUsers,
                "candidates", candidates,
                "recruiters", recruiters,
                "admins", admins
        ));
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getRevenueStats() {
        Double totalRevenue = paymentRepository.getTotalRevenue();
        if (totalRevenue == null) {
            totalRevenue = 0.0;
        }
        long completedPayments = paymentRepository.countByStatus(Payment.PaymentStatus.COMPLETED);
        return ResponseEntity.ok(Map.of(
                "totalRevenue", BigDecimal.valueOf(totalRevenue),
                "completedPayments", completedPayments
        ));
    }
}

