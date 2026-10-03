package com.bankify.transaction.service;

import java.time.LocalDateTime;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bankify.transaction.dto.TransactionAuditResponse;
import com.bankify.transaction.dto.TransactionRequest;
import com.bankify.transaction.dto.TransactionResponse;

import com.bankify.transaction.enums.TransactionStatus;
import com.bankify.transaction.enums.TransactionType;

public interface TransactionService {

    TransactionResponse createTransaction(TransactionRequest request);

    TransactionResponse getTransactionById(Long id);

    TransactionResponse getTransactionByReference(String transactionReference);

    Page<TransactionResponse> getAllTransactions(Pageable pageable);
    
    Page<TransactionResponse> getTransactionsBySenderAccountId(Long senderAccountId, Pageable pageable);
    
    Page<TransactionResponse> getTransactionsByReceiverAccountId(Long receiverAccountId, Pageable pageable);
    
    Page<TransactionResponse> getTransactionsByStatus(TransactionStatus status, Pageable pageable);
    
    Page<TransactionResponse> getTransactionsByType(TransactionType transactionType, Pageable page);
    
    Page<TransactionResponse> getTransactionsByDateRange(LocalDateTime from, LocalDateTime to, Pageable pageable);
    
    Page<TransactionResponse> getTransactionsByAccountAndDateRange(
    	    Long accountId,
    	    LocalDateTime from,
    	    LocalDateTime to,
    	    Pageable pageable
   	);
    
    Page<TransactionResponse> getTransactionsByAccountId(Long accountId, Pageable pageable);
    
    TransactionResponse recoverPendingCompensation(Long transactionId);
    
    Page<TransactionAuditResponse> getTransactionAuditHistory(
            Long transactionId,
            int page,
            int size
    );

    
    
}