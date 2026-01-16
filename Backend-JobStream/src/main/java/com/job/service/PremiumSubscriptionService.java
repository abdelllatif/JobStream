package com.job.service;

import com.job.entity.Payment;
import com.job.entity.PremiumSubscription;
import com.job.enums.PlanType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PremiumSubscriptionService {
    // Subscription management with payment integration
    Map<String, Object> createSubscriptionWithPayment(Long userId, PlanType planType);
    
    // Subscription management
    PremiumSubscription updateSubscription(Long subscriptionId, PlanType planType);
    void cancelSubscription(Long subscriptionId);
    Optional<PremiumSubscription> getActiveSubscription(Long userId);
    List<PremiumSubscription> getUserSubscriptions(Long userId);
    boolean isUserPremium(Long userId);
    void checkAndExpireSubscriptions();
    
    // Payment delegation methods
    default Payment createPayment(Long userId, PlanType planType, BigDecimal amount, String currency) {
        throw new UnsupportedOperationException("Payment operations should be handled by PayPalService");
    }
    
    default String approvePayment(String paymentId) {
        throw new UnsupportedOperationException("Payment operations should be handled by PayPalService");
    }
    
    default Payment executePayment(String paymentId, String payerId) {
        throw new UnsupportedOperationException("Payment operations should be handled by PayPalService");
    }
    
    default List<Payment> getUserPayments(Long userId) {
        throw new UnsupportedOperationException("Payment operations should be handled by PayPalService");
    }
    
    default void refundPayment(String paymentId) {
        throw new UnsupportedOperationException("Payment operations should be handled by PayPalService");
    }
    
    // Utility methods
    Map<String, Object> getAvailablePlans();
}
