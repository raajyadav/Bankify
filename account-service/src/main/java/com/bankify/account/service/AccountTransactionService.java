package com.bankify.account.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.retry.annotation.Retryable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankify.account.entity.Account;
import com.bankify.account.entity.AccountOperation;
import com.bankify.account.exception.AccountNotActiveException;
import com.bankify.account.exception.AccountNotFoundException;
import com.bankify.account.exception.InsufficientBalanceException;
import com.bankify.account.exception.InvalidAccountAmountException;
import com.bankify.account.repository.AccountOperationRepository;
import com.bankify.account.repository.AccountRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountTransactionService {
	

    private final AccountRepository accountRepository;
    
    private final AccountOperationRepository accountOperationRepository;
    
    @Retryable(
            retryFor = {
                    org.springframework.dao.DeadlockLoserDataAccessException.class
            },
            maxAttempts = 3
          
    )
    @Transactional
    public void debitAccount(Long accountId, BigDecimal amount) {
    	
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found with id: " + accountId
                        )
                );

        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            throw new AccountNotActiveException(
                    "Account is not active"
            );
        }

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidAccountAmountException(
                    "Amount must be greater than zero"
            );
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance in account"
            );
        }

        BigDecimal newBalance =
                account.getBalance().subtract(amount);

        account.setBalance(newBalance);
        account.setUpdatedAt(LocalDateTime.now());

        accountRepository.save(account);
    }
    
    @Retryable(
            retryFor = {
                    org.springframework.dao.DeadlockLoserDataAccessException.class
            },
            maxAttempts = 3
    )
    @Transactional
    public void creditAccount(
            Long accountId,
            BigDecimal amount,
            String operationKey,
            String operationType) {
    	


        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with id: " + accountId));

        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            throw new AccountNotActiveException(
                    "Account is not active");
        }

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new InvalidAccountAmountException(
                    "Amount must be greater than zero");
        }

        // Idempotency check
        if (accountOperationRepository
                .existsByOperationKey(operationKey)) {
            return;
        }

        // Credit account
        BigDecimal newBalance =
                account.getBalance().add(amount);

        account.setBalance(newBalance);
        account.setUpdatedAt(LocalDateTime.now());

        accountRepository.save(account);

        // Record completed operation
        AccountOperation operation = new AccountOperation();

        operation.setOperationKey(operationKey);
        operation.setAccountId(accountId);
        operation.setOperationType(operationType);
        operation.setAmount(amount);
        operation.setCreatedAt(LocalDateTime.now());

        accountOperationRepository.save(operation);
    }
}