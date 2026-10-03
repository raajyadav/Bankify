package com.bankify.transaction.client;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bankify.transaction.dto.AccountResponse;

@FeignClient(name = "account-service")
public interface AccountClient {

    @GetMapping("/api/accounts/{id}")
    AccountResponse getAccountById(@PathVariable Long id);

    @PostMapping("/api/accounts/{id}/debit")
    AccountResponse debitAccount(
            @PathVariable Long id,
            @RequestParam BigDecimal amount);

    @PostMapping("/api/accounts/{id}/credit")
    AccountResponse creditAccount(
            @PathVariable Long id,
            @RequestParam BigDecimal amount,
            @RequestParam String operationKey,
            @RequestParam String operationType);
}