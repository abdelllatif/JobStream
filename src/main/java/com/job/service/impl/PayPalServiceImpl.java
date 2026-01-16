package com.job.service.impl;

import com.job.entity.Payment;
import com.job.entity.PremiumSubscription;
import com.job.entity.User;
import com.job.enums.PlanType;
import com.job.entity.Payment.PaymentStatus;
import com.job.repository.PaymentRepository;
import com.job.repository.PremiumSubscriptionRepository;
import com.job.repository.UserRepository;
import com.job.service.PayPalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayPalServiceImpl implements PayPalService {

    private final PaymentRepository paymentRepository;
    private final PremiumSubscriptionRepository premiumSubscriptionRepository;
    private final UserRepository userRepository;

    @Value("${paypal.mode:sandbox}")
    private String paypalMode;

    @Override
    @Transactional
    public Payment createPayment(Long userId, PlanType planType, BigDecimal amount, String currency) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setPlanType(planType);
        payment.setAmount(amount);
        payment.setCurrency(currency);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaypalOrderId("ORDER-" + UUID.randomUUID().toString());
        payment.setPaypalPaymentId("PAY-" + UUID.randomUUID().toString());
        payment.setPaypalTransactionId("TXN-" + UUID.randomUUID().toString());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Created PayPal payment for user {} with plan {}: {}", userId, planType, savedPayment.getId());
        return savedPayment;
    }

    @Override
    public String approvePayment(String paymentId) {
        // In production, this would call PayPal API to get approval URL
        String approvalUrl = "https://www." + paypalMode + ".paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=" + paymentId;
        log.info("Generated approval URL for payment {}: {}", paymentId, approvalUrl);
        return approvalUrl;
    }

    @Override
    @Transactional
    public Payment executePayment(String paymentId, String payerId) {
        Optional<Payment> paymentOpt = paymentRepository.findByPaypalPaymentId(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new RuntimeException("Payment not found");
        }

        Payment payment = paymentOpt.get();
        
        // In production, this would call PayPal API to execute the payment
        // For now, we'll simulate successful execution
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setUpdatedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        
        // Process the successful payment
        processSuccessfulPayment(savedPayment);
        
        log.info("Executed PayPal payment {}: {}", paymentId, savedPayment.getStatus());
        return savedPayment;
    }

    @Override
    @Transactional
    public Payment capturePayment(String orderId) {
        Optional<Payment> paymentOpt = paymentRepository.findByPaypalOrderId(orderId);
        if (paymentOpt.isEmpty()) {
            throw new RuntimeException("Payment not found");
        }

        Payment payment = paymentOpt.get();
        
        // In production, this would call PayPal API to capture the payment
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setUpdatedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        
        // Process the successful payment
        processSuccessfulPayment(savedPayment);
        
        log.info("Captured PayPal payment for order {}: {}", orderId, savedPayment.getStatus());
        return savedPayment;
    }

    @Override
    @Transactional
    public void refundPayment(String paymentId) {
        Optional<Payment> paymentOpt = paymentRepository.findByPaypalTransactionId(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new RuntimeException("Payment not found");
        }

        Payment payment = paymentOpt.get();
        
        // In production, this would call PayPal API to process the refund
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setUpdatedAt(LocalDateTime.now());

        paymentRepository.save(payment);
        
        // Cancel the premium subscription if exists
        Optional<PremiumSubscription> subscriptionOpt = premiumSubscriptionRepository
                .findByUserIdAndActiveTrue(payment.getUser().getId());
        if (subscriptionOpt.isPresent()) {
            PremiumSubscription subscription = subscriptionOpt.get();
            subscription.setActive(false);
            subscription.setUpdatedAt(LocalDateTime.now());
            premiumSubscriptionRepository.save(subscription);
            
            User user = payment.getUser();
            user.setPremiumUser(false);
            userRepository.save(user);
        }
        
        log.info("Refunded PayPal payment: {}", paymentId);
    }

    @Override
    public Optional<Payment> getPaymentByPaypalId(String paypalPaymentId) {
        return paymentRepository.findByPaypalPaymentId(paypalPaymentId);
    }

    @Override
    public Optional<Payment> getPaymentByOrderId(String orderId) {
        return paymentRepository.findByPaypalOrderId(orderId);
    }

    @Override
    public List<Payment> getUserPayments(Long userId) {
        return paymentRepository.findByUserIdOrderByPaidAtDesc(userId);
    }

    @Override
    @Transactional
    public PremiumSubscription processSuccessfulPayment(Payment payment) {
        // Create or update premium subscription
        Optional<PremiumSubscription> existingSubscription = premiumSubscriptionRepository
                .findByUserIdAndActiveTrue(payment.getUser().getId());

        PremiumSubscription subscription;
        if (existingSubscription.isPresent()) {
            subscription = existingSubscription.get();
            // Update existing subscription
            subscription.setPlanType(payment.getPlanType());
            subscription.setEndDate(calculateEndDate(payment.getPlanType()));
            subscription.setAmount(payment.getAmount());
            subscription.setCurrency(payment.getCurrency());
            subscription.setUpdatedAt(LocalDateTime.now());
        } else {
            // Create new subscription
            subscription = new PremiumSubscription();
            subscription.setUser(payment.getUser());
            subscription.setPlanType(payment.getPlanType());
            subscription.setStartDate(LocalDateTime.now());
            subscription.setEndDate(calculateEndDate(payment.getPlanType()));
            subscription.setActive(true);
            subscription.setAmount(payment.getAmount());
            subscription.setCurrency(payment.getCurrency());
            subscription.setPayment(payment);
            subscription.setCreatedAt(LocalDateTime.now());
            subscription.setUpdatedAt(LocalDateTime.now());
        }

        // Update user premium status
        User user = payment.getUser();
        user.setPremiumUser(true);
        userRepository.save(user);

        PremiumSubscription savedSubscription = premiumSubscriptionRepository.save(subscription);
        log.info("Processed successful payment and created premium subscription for user: {}", payment.getUser().getId());
        
        return savedSubscription;
    }

    @Override
    public void processFailedPayment(String paymentId, String reason) {
        Optional<Payment> paymentOpt = paymentRepository.findByPaypalTransactionId(paymentId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus(PaymentStatus.FAILED);
            payment.setUpdatedAt(LocalDateTime.now());
            paymentRepository.save(payment);
            
            log.error("Payment failed for {}: {}", paymentId, reason);
        }
    }

    @Override
    public boolean validateWebhookSignature(String payload, String signature) {
        // In production, this would validate the PayPal webhook signature
        // For now, we'll return true for simplicity
        log.info("Validating PayPal webhook signature");
        return true;
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
}
