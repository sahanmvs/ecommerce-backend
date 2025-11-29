package com.mvs.payment_service.model;

import com.mvs.payment_service.dto.OrderResponse;
import com.mvs.payment_service.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Document("paymentRecords")
public class PaymentRecord {
    @Id
    private String id;
    private String orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    @Builder.Default
    private Map<String, String> metadata =  new HashMap<>();
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}
