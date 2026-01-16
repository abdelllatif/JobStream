package com.job.service;

import com.job.entity.Payment;

import java.util.List;

public interface PaymentService {
    Payment create(Long userId, String paypalTransactionId);
    Payment getById(Long id);
    List<Payment> getAll();
    Payment updateStatus(Long id, boolean success);
    void delete(Long id);
}

