package com.bankify.transaction.controller;

import java.time.LocalDateTime;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bankify.transaction.dto.TransactionAuditResponse;
import com.bankify.transaction.dto.TransactionRequest;
import com.bankify.transaction.dto.TransactionResponse;
import com.bankify.transaction.entity.TransactionAudit;
import com.bankify.transaction.enums.TransactionStatus;
import com.bankify.transaction.enums.TransactionType;
import com.bankify.transaction.service.TransactionService;
import com.bankify.transaction.util.PaginationValidator;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
           @Valid @RequestBody TransactionRequest request) {

        TransactionResponse response =
                transactionService.createTransaction(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable Long id) {

        TransactionResponse response =
                transactionService.getTransactionById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/reference/{transactionReference}")
    public ResponseEntity<TransactionResponse> getTransactionByReference(
            @PathVariable String transactionReference) {

        TransactionResponse response =
                transactionService.getTransactionByReference(
                        transactionReference);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Pageable pageable) {

        PaginationValidator.validate(page, size);

        return ResponseEntity.ok(
                transactionService.getAllTransactions(pageable)
        );
    }
    
    @GetMapping("/sender/{senderAccountId}")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsBySenderAccountId(
    		 @RequestParam(defaultValue = "0") int page,
             @RequestParam(defaultValue = "10") int size,
            @PathVariable Long senderAccountId,
            Pageable pageable) {
    	
    	PaginationValidator.validate(page, size);

        return ResponseEntity.ok(
                transactionService.getTransactionsBySenderAccountId(senderAccountId, pageable)
        );
    }
    
    @GetMapping("/receiver/{receiverAccountId}")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByReceiverAccountId(
    		 @RequestParam(defaultValue = "0") int page,
             @RequestParam(defaultValue = "10") int size,
            @PathVariable Long receiverAccountId,
            Pageable pageable) {
    	
    	PaginationValidator.validate(page, size);

        return ResponseEntity.ok(
                transactionService.getTransactionsByReceiverAccountId(receiverAccountId, pageable)
        );
    }
    
//    @GetMapping("/account/{accountId}")
//    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccountId(
//            @PathVariable Long accountId) {
//
//        return ResponseEntity.ok(
//                transactionService.getTransactionsByAccountId(accountId)
//        );
//    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByStatus(
     		@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable TransactionStatus status,
            Pageable pageable) {
    	
    	PaginationValidator.validate(page, size);

        return ResponseEntity.ok(
                transactionService.getTransactionsByStatus(status, pageable)
        );
    }
    
    @GetMapping("/type/{transactionType}")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByType(
     	    @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable TransactionType transactionType, 
            Pageable pageable) {
    	
    	PaginationValidator.validate(page, size);

        return ResponseEntity.ok(
                transactionService.getTransactionsByType(transactionType, pageable)
        );
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByDateRange(
     	    @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,	
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to,
            Pageable pageable
    		) {
    	
    	PaginationValidator.validate(page, size);

        return ResponseEntity.ok(
                transactionService.getTransactionsByDateRange(from, to, pageable)
        );
    }
    
    @GetMapping("/account/{accountId}/date-range")
    public ResponseEntity<Page<TransactionResponse>>
    getTransactionsByAccountAndDateRange(
     	    @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,	
            @PathVariable Long accountId,
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to,
            Pageable pageable) {
    	
    	PaginationValidator.validate(page, size);

        return ResponseEntity.ok(
                transactionService.getTransactionsByAccountAndDateRange(
                        accountId,
                        from,
                        to,
                        pageable
                )
        );
    }
    
    @GetMapping("/account/{accountId}")
    public ResponseEntity<Page<TransactionResponse>> getTransactionsByAccountId(
     	    @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable Long accountId,
            Pageable pageable) {
    	
    	PaginationValidator.validate(page, size);

        return ResponseEntity.ok(
                transactionService.getTransactionsByAccountId(
                        accountId,
                        pageable
                )
        );
    }
    
    @PostMapping("/{id}/recover")
    public ResponseEntity<TransactionResponse> recoverPendingCompensation(
            @PathVariable Long id) {

        TransactionResponse response =
                transactionService.recoverPendingCompensation(id);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/audit")
    public ResponseEntity<Page<TransactionAuditResponse>> getTransactionAuditHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                transactionService.getTransactionAuditHistory(
                        id,
                        page,
                        size
                )
        );
    }
}