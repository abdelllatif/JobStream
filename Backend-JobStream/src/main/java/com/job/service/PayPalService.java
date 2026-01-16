package com.job.service;

import com.job.entity.Payment;
import com.job.entity.PremiumSubscription;
import com.job.enums.PlanType;
import com.job.entity.Payment.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PayPalService {
    Payment createPayment(Long userId, PlanType planType, BigDecimal amount, String currency);
    String approvePayment(String paymentId);
    Payment executePayment(String paymentId, String payerId);
    Payment capturePayment(String orderId);
    void refundPayment(String paymentId);
    Optional<Payment> getPaymentByPaypalId(String paypalPaymentId);
    Optional<Payment> getPaymentByOrderId(String orderId);
    List<Payment> getUserPayments(Long userId);
    PremiumSubscription processSuccessfulPayment(Payment payment);
    void processFailedPayment(String paymentId, String reason);
    boolean validateWebhookSignature(String payload, String signature);
}
