package com.bankify.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bankify.transaction.enums.TransactionChannel;
import com.bankify.transaction.enums.TransactionStatus;
import com.bankify.transaction.enums.TransactionType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionResponse {

    private Long id;

    private String transactionReference;

    private Long senderAccountId;

    private Long receiverAccountId;

    private TransactionType transactionType;

    private BigDecimal amount;

    private TransactionStatus status;

    private TransactionChannel channel;

    private String description;

    private String remarks;

    private String initiatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}