package com.bankify.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountResponse {

    private Long id;

    private String accountNumber;

    private String accountType;

    private BigDecimal balance;

    private String status;

    private Long customerId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}