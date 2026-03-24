package com.job.controller;

import com.job.entity.Payment;
import com.job.entity.PremiumSubscription;
import com.job.enums.PlanType;
import com.job.service.PayPalService;
import com.job.service.PremiumSubscriptionService;
import com.job.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/premium")
@RequiredArgsConstructor
@Slf4j
public class PremiumSubscriptionController {

    private final PremiumSubscriptionService premiumSubscriptionService;
    private final PayPalService payPalService;
    private final AuthUtil authUtil;

    @PostMapping("/subscribe")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<Map<String, Object>> subscribe(@RequestParam PlanType planType) {
        try {
            Long userId = authUtil.getCurrentUserId();
            Map<String, Object> result = premiumSubscriptionService.createSubscriptionWithPayment(userId, planType);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error creating subscription with payment for current user with plan {}: {}", planType, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/payment/approve/{paymentId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<String> approvePayment(@PathVariable String paymentId) {
        try {
            String approvalUrl = payPalService.approvePayment(paymentId);
            return ResponseEntity.ok(approvalUrl);
        } catch (Exception e) {
            log.error("Error approving payment {}: {}", paymentId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/payment/execute/{paymentId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<Payment> executePayment(
            @PathVariable String paymentId,
            @RequestParam String payerId) {
        try {
            Payment payment = payPalService.executePayment(paymentId, payerId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            log.error("Error executing payment {}: {}", paymentId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update/{subscriptionId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<PremiumSubscription> updateSubscription(
            @PathVariable Long subscriptionId, 
            @RequestParam PlanType planType) {
        try {
            PremiumSubscription subscription = premiumSubscriptionService.updateSubscription(subscriptionId, planType);
            return ResponseEntity.ok(subscription);
        } catch (Exception e) {
            log.error("Error updating subscription {}: {}", subscriptionId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/cancel/{subscriptionId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    public ResponseEntity<Void> cancelSubscription(@PathVariable Long subscriptionId) {
        try {
            premiumSubscriptionService.cancelSubscription(subscriptionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error cancelling subscription {}: {}", subscriptionId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/active/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<PremiumSubscription> getActiveSubscription(@PathVariable Long userId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        return premiumSubscriptionService.getActiveSubscription(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<PremiumSubscription>> getUserSubscriptions(@PathVariable Long userId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        List<PremiumSubscription> subscriptions = premiumSubscriptionService.getUserSubscriptions(userId);
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/check/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Boolean> checkPremiumStatus(@PathVariable Long userId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        boolean isPremium = premiumSubscriptionService.isUserPremium(userId);
        return ResponseEntity.ok(isPremium);
    }

    @PostMapping("/expire-check")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> checkAndExpireSubscriptions() {
        premiumSubscriptionService.checkAndExpireSubscriptions();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/payments/user/{userId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<List<Payment>> getUserPayments(@PathVariable Long userId) {
        Long currentUserId = authUtil.getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            return ResponseEntity.badRequest().build();
        }
        List<Payment> payments = payPalService.getUserPayments(userId);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/payment/refund/{paymentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> refundPayment(@PathVariable String paymentId) {
        try {
            payPalService.refundPayment(paymentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error refunding payment {}: {}", paymentId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/plans")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getAvailablePlans() {
        Map<String, Object> plans = premiumSubscriptionService.getAvailablePlans();
        return ResponseEntity.ok(plans);
    }
}
