package com.bankify.transaction.dto;

import java.time.LocalDateTime;

import com.bankify.transaction.enums.TransactionStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionAuditResponse {

    private Long id;

    private Long transactionId;

    private String transactionReference;

    private TransactionStatus previousStatus;

    private TransactionStatus newStatus;

    private LocalDateTime changedAt;

    private String reason;
}