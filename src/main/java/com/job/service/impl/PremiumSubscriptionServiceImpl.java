package com.job.service.impl;

import com.job.entity.Payment;
import com.job.entity.PremiumSubscription;
import com.job.entity.User;
import com.job.enums.PlanType;
import com.job.repository.PremiumSubscriptionRepository;
import com.job.repository.UserRepository;
import com.job.service.PayPalService;
import com.job.service.PremiumSubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PremiumSubscriptionServiceImpl implements PremiumSubscriptionService {

    private final PremiumSubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final PayPalService payPalService;

    @Override
    @Transactional
    public Map<String, Object> createSubscriptionWithPayment(Long userId, PlanType planType) {
        try {
            Optional<PremiumSubscription> existingSubscription = getActiveSubscription(userId);
            if (existingSubscription.isPresent()) {
                throw new RuntimeException("User already has an active premium subscription");
            }

            BigDecimal amount = calculateAmount(planType);
            
            Payment payment = payPalService.createPayment(userId, planType, amount, "USD");
            String approvalUrl = payPalService.approvePayment(payment.getPaypalPaymentId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("payment", payment);
            response.put("approvalUrl", approvalUrl);
            response.put("message", "Payment created successfully. Please complete the payment.");
            
            return response;
        } catch (Exception e) {
            log.error("Error creating subscription with payment for user {} with plan {}: {}", userId, planType, e.getMessage());
            throw new RuntimeException("Failed to create subscription with payment: " + e.getMessage());
        }
    }
    @Override
    public Payment createPayment(Long userId, PlanType planType, BigDecimal amount, String currency) {
        return payPalService.createPayment(userId, planType, amount, currency);
    }

    @Override
    public String approvePayment(String paymentId) {
        return payPalService.approvePayment(paymentId);
    }

    @Override
    public Payment executePayment(String paymentId, String payerId) {
        return payPalService.executePayment(paymentId, payerId);
    }

    @Override
    public List<Payment> getUserPayments(Long userId) {
        return payPalService.getUserPayments(userId);
    }

    @Override
    public void refundPayment(String paymentId) {
        payPalService.refundPayment(paymentId);
    }

    @Override
    @Transactional
    public PremiumSubscription updateSubscription(Long subscriptionId, PlanType planType) {
        PremiumSubscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        subscription.setPlanType(planType);
        subscription.setEndDate(calculateEndDate(planType));
        subscription.setAmount(calculateAmount(planType));
        subscription.setUpdatedAt(LocalDateTime.now());

        return subscriptionRepository.save(subscription);
    }

    @Override
    @Transactional
    public void cancelSubscription(Long subscriptionId) {
        PremiumSubscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        subscription.setActive(false);
        subscription.setUpdatedAt(LocalDateTime.now());

        User user = subscription.getUser();
        user.setPremiumUser(false);
        userRepository.save(user);

        subscriptionRepository.save(subscription);
        log.info("Cancelled premium subscription {}", subscriptionId);
    }

    @Override
    public Optional<PremiumSubscription> getActiveSubscription(Long userId) {
        return subscriptionRepository.findByUserIdAndActiveTrue(userId);
    }

    @Override
    public List<PremiumSubscription> getUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public boolean isUserPremium(Long userId) {
        return getActiveSubscription(userId).isPresent();
    }

    @Override
    @Transactional
    public void checkAndExpireSubscriptions() {
        List<PremiumSubscription> expiredSubscriptions = subscriptionRepository
                .findByActiveTrueAndEndDateBefore(LocalDateTime.now());

        for (PremiumSubscription subscription : expiredSubscriptions) {
            subscription.setActive(false);
            subscription.setUpdatedAt(LocalDateTime.now());

            User user = subscription.getUser();
            user.setPremiumUser(false);
            userRepository.save(user);

            subscriptionRepository.save(subscription);
            log.info("Expired premium subscription for user {}", user.getId());
        }
    }


    @Override
    public Map<String, Object> getAvailablePlans() {
        Map<String, Object> plans = new HashMap<>();
        plans.put("BASIC", Map.of("name", "Basic", "price", 0.00, "duration", "lifetime"));
        plans.put("PREMIUM_MONTHLY", Map.of("name", "Premium Monthly", "price", 9.99, "duration", "1 month"));
        plans.put("PREMIUM_YEARLY", Map.of("name", "Premium Yearly", "price", 99.99, "duration", "1 year"));
        plans.put("PREMIUM_PLUS", Map.of("name", "Premium Plus", "price", 49.99, "duration", "6 months"));
        return plans;
    }


    private LocalDateTime calculateEndDate(PlanType planType) {
        LocalDateTime now = LocalDateTime.now();
        return switch (planType) {
            case PREMIUM_MONTHLY -> now.plusMonths(1);
            case PREMIUM_YEARLY -> now.plusYears(1);
            case PREMIUM_PLUS -> now.plusMonths(6);
            default -> now.plusMonths(1);
        };
    }

    private BigDecimal calculateAmount(PlanType planType) {
        return switch (planType) {
            case PREMIUM_MONTHLY -> BigDecimal.valueOf(9.99);
            case PREMIUM_YEARLY -> BigDecimal.valueOf(99.99);
            case PREMIUM_PLUS -> BigDecimal.valueOf(49.99);
            default -> BigDecimal.valueOf(9.99);
        };
    }
}
