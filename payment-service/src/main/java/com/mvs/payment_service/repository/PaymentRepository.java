package com.mvs.payment_service.repository;

import com.mvs.payment_service.enums.PaymentStatus;
import com.mvs.payment_service.model.PaymentRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends MongoRepository<PaymentRecord, String> {
    Optional<PaymentRecord> findPaymentRecordByOrderId(String orderId);

    Optional<PaymentRecord> findPaymentRecordByOrderIdAndStatus(String orderId, PaymentStatus status);
}
