package com.bankify.transaction.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bankify.transaction.entity.TransactionAudit;

public interface TransactionAuditRepository
        extends JpaRepository<TransactionAudit, Long> {

    List<TransactionAudit> findByTransactionIdOrderByChangedAtAsc(
            Long transactionId
    );
    
    Page<TransactionAudit> findByTransactionIdOrderByChangedAtAsc(
            Long transactionId,
            Pageable pageable
    );
}