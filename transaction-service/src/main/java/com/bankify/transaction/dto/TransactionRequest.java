package com.bankify.transaction.dto;

import java.math.BigDecimal;


import com.bankify.transaction.enums.TransactionChannel;
import com.bankify.transaction.enums.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class TransactionRequest {

    @NotNull(message = "Sender account ID is required")
    private Long senderAccountId;

    @NotNull(message = "Receiver account ID is required")
    private Long receiverAccountId;

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Channel is required")
    private TransactionChannel channel;

    private String description;

    private String remarks;

    private String initiatedBy;
    
    private String idempotencyKey;
}