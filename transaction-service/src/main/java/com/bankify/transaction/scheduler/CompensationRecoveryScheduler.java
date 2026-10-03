package com.bankify.transaction.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bankify.transaction.enums.TransactionStatus;
import com.bankify.transaction.repository.TransactionRepository;
import com.bankify.transaction.service.TransactionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompensationRecoveryScheduler {

    private final TransactionRepository transactionRepository;

    private final TransactionService transactionService;

    @Scheduled(fixedDelay = 60000)
    public void recoverPendingCompensations() {

        log.info("Compensation recovery scheduler started");

        var pendingTransactions =
                transactionRepository
                        .findByStatusOrderByCreatedAtDesc(
                                TransactionStatus.COMPENSATION_PENDING,
                                org.springframework.data.domain.PageRequest.of(0, 10)
                        );

        log.info(
                "Pending compensation transactions found: {}",
                pendingTransactions.getTotalElements()
        );

        pendingTransactions.forEach(transaction -> {

            try {

                log.info(
                        "Attempting compensation recovery for transaction: {}",
                        transaction.getTransactionReference()
                );

                transactionService.recoverPendingCompensation(
                        transaction.getId()
                );

                log.info(
                        "Compensation recovery completed for transaction: {}",
                        transaction.getTransactionReference()
                );

            } catch (Exception ex) {

                log.error(
                        "Compensation recovery failed for transaction: {}. It will be retried later.",
                        transaction.getTransactionReference(),
                        ex
                );
            }
        });

        log.info("Compensation recovery scheduler finished");
    }
}