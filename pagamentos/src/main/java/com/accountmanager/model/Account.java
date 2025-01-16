package com.accountmanager.model;

import lombok.Data;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class Account {
    private String id;
    private String ownerName;
    private BigDecimal balance;
    private BigDecimal expectedPayment;
    private LocalDateTime paymentDueDate;
    private boolean blocked;
    private LocalDateTime blockedDate;
    private String blockReason;
}