package com.bankify.transaction.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bankify.transaction.entity.Transaction;
import com.bankify.transaction.enums.TransactionStatus;
import com.bankify.transaction.enums.TransactionType;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionReference(String transactionReference);

    boolean existsByTransactionReference(String transactionReference);

    Page<Transaction> findBySenderAccountIdOrderByCreatedAtDesc(Long senderAccountId, Pageable pageable);

    Page<Transaction> findByReceiverAccountIdOrderByCreatedAtDesc(Long receiverAccountId, Pageable pageable);
    
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
    
    Page<Transaction> findByStatusOrderByCreatedAtDesc(TransactionStatus status, Pageable pageable);
    
    Page<Transaction> findByTransactionTypeOrderByCreatedAtDesc(TransactionType transactionType, Pageable pageable);
    
    Page<Transaction> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime from, LocalDateTime to, Pageable pageable);
    
    Page<Transaction> findBySenderAccountIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long senderAccountId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    Page<Transaction> findByReceiverAccountIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long receiverAccountId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );
    
    @Query("""
            SELECT t
            FROM Transaction t
            WHERE t.senderAccountId = :accountId
               OR t.receiverAccountId = :accountId
            ORDER BY t.createdAt DESC
            """)
    Page<Transaction> findByAccountId(
            @Param("accountId") Long accountId,
            Pageable pageable
    );
    
    @Query("""
            SELECT t
            FROM Transaction t
            WHERE (t.senderAccountId = :accountId
               OR t.receiverAccountId = :accountId)
              AND t.createdAt BETWEEN :from AND :to
            ORDER BY t.createdAt DESC
            """)
    Page<Transaction> findByAccountIdAndCreatedAtBetween(
            @Param("accountId") Long accountId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );
    
    Page<Transaction> findAll(Pageable pageable);
    
}