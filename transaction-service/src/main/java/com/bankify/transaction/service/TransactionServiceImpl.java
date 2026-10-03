package com.bankify.transaction.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bankify.transaction.client.AccountClient;
import com.bankify.transaction.dto.AccountResponse;
import com.bankify.transaction.dto.TransactionAuditResponse;
import com.bankify.transaction.dto.TransactionRequest;
import com.bankify.transaction.dto.TransactionResponse;
import com.bankify.transaction.entity.Transaction;
import com.bankify.transaction.entity.TransactionAudit;
import com.bankify.transaction.enums.TransactionStatus;
import com.bankify.transaction.enums.TransactionType;
import com.bankify.transaction.exception.AccountNotActiveException;
import com.bankify.transaction.exception.DuplicateTransactionException;
import com.bankify.transaction.exception.InsufficientBalanceException;
import com.bankify.transaction.exception.InvalidDateRangeException;
import com.bankify.transaction.exception.InvalidTransactionAmountException;
import com.bankify.transaction.exception.InvalidTransactionException;
import com.bankify.transaction.exception.TransactionNotFoundException;
import com.bankify.transaction.repository.TransactionAuditRepository;
import com.bankify.transaction.repository.TransactionRepository;
import com.bankify.transaction.util.PaginationValidator;
import com.bankify.transaction.validator.TransactionStatusValidator;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

	private final TransactionRepository transactionRepository;

	private final AccountClient accountClient;
	
	private final TransactionStatusValidator transactionStatusValidator;
	
	private final TransactionAuditRepository transactionAuditRepository;

	@Override
	public TransactionResponse createTransaction(TransactionRequest request) {

	    // 1. Validate transaction amount
	    if (request.getAmount() == null ||
	            request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

	        throw new InvalidTransactionAmountException(
	                "Transaction amount must be greater than zero"
	        );
	    }

	    // 2. Validate idempotency key
	    if (request.getIdempotencyKey() == null ||
	            request.getIdempotencyKey().isBlank()) {

	        throw new InvalidTransactionException(
	                "Idempotency key is required"
	        );
	    }

	    // 3. Check for duplicate transaction
	    Transaction existingTransaction =
	            transactionRepository
	                    .findByIdempotencyKey(request.getIdempotencyKey())
	                    .orElse(null);

	    if (existingTransaction != null) {
	        return mapToResponse(existingTransaction);
	    }

	    // 4. Validate sender and receiver are different
	    if (request.getSenderAccountId()
	            .equals(request.getReceiverAccountId())) {

	        throw new InvalidTransactionException(
	                "Sender and receiver accounts cannot be the same"
	        );
	    }

	    // 5. Get sender and receiver accounts
	    AccountResponse senderAccount =
	            accountClient.getAccountById(
	                    request.getSenderAccountId()
	            );

	    AccountResponse receiverAccount =
	            accountClient.getAccountById(
	                    request.getReceiverAccountId()
	            );

	    // 6. Validate sender account status
	    if (!"ACTIVE".equalsIgnoreCase(senderAccount.getStatus())) {

	        throw new AccountNotActiveException(
	                "Sender account is not active"
	        );
	    }

	    // 7. Validate receiver account status
	    if (!"ACTIVE".equalsIgnoreCase(receiverAccount.getStatus())) {

	        throw new AccountNotActiveException(
	                "Receiver account is not active"
	        );
	    }

	    // 8. Check sender balance
	    if (senderAccount.getBalance()
	            .compareTo(request.getAmount()) < 0) {

	        throw new InsufficientBalanceException(
	                "Insufficient balance in sender account"
	        );
	    }

	    // 9. Create transaction with PENDING status
	    Transaction transaction = new Transaction();

	    transaction.setTransactionReference(
	            generateTransactionReference()
	    );

	    transaction.setIdempotencyKey(
	            request.getIdempotencyKey()
	    );

	    transaction.setSenderAccountId(
	            request.getSenderAccountId()
	    );

	    transaction.setReceiverAccountId(
	            request.getReceiverAccountId()
	    );

	    transaction.setTransactionType(
	            request.getTransactionType()
	    );

	    transaction.setAmount(
	            request.getAmount()
	    );

	    transaction.setStatus(
	            TransactionStatus.PENDING
	    );

	    transaction.setChannel(
	            request.getChannel()
	    );

	    transaction.setDescription(
	            request.getDescription()
	    );

	    transaction.setRemarks(
	            request.getRemarks()
	    );

	    transaction.setInitiatedBy(
	            request.getInitiatedBy()
	    );

	    LocalDateTime now = LocalDateTime.now();

	    transaction.setCreatedAt(now);
	    transaction.setUpdatedAt(now);

	    // 10. Save PENDING transaction
	    Transaction savedTransaction;

	    try {

	        savedTransaction =
	                transactionRepository.save(transaction);

	    } catch (DataIntegrityViolationException ex) {

	        throw new DuplicateTransactionException(
	                "Transaction with this idempotency key already exists"
	        );
	    }

	    // 11. Track whether debit was successful
	    boolean debitSuccessful = false;

	    try {

	        // 12. Debit sender
	        AccountResponse updatedSenderAccount =
	                accountClient.debitAccount(
	                        request.getSenderAccountId(),
	                        request.getAmount()
	                );

	        log.info(
	                "Sender account {} debited successfully for amount {}",
	                request.getSenderAccountId(),
	                request.getAmount()
	        );

	        debitSuccessful = true;

	     // 13. Credit receiver
	        accountClient.creditAccount(
	                request.getReceiverAccountId(),
	                request.getAmount(),
	                request.getIdempotencyKey() + "-RECEIVER",
	                "RECEIVER_CREDIT"
	        );

	        log.info(
	                "Receiver account {} credited successfully for amount {}",
	                request.getReceiverAccountId(),
	                request.getAmount()
	        );

	        // 14. Both operations succeeded
	        changeTransactionStatus(
	                transaction,
	                TransactionStatus.SUCCESS
	        );

	        transaction.setUpdatedAt(
	                LocalDateTime.now()
	        );

	        savedTransaction =
	                transactionRepository.save(transaction);

	    } catch (Exception ex) {

	        log.error(
	                "Transaction failed for sender account {} and receiver account {}",
	                request.getSenderAccountId(),
	                request.getReceiverAccountId(),
	                ex
	        );

	        log.info(
	                "Debit successful: {}",
	                debitSuccessful
	        );

	        // 15. Compensation
	        boolean compensationSuccessful = false;

	        if (debitSuccessful) {

	            try {

	                log.info(
	                        "Starting compensation for sender account: {}",
	                        request.getSenderAccountId()
	                );

	                String operationKey =
	                        transaction.getTransactionReference() + "-COMPENSATION";

	                AccountResponse compensatedAccount =
	                		accountClient.creditAccount(
	                		        request.getSenderAccountId(),
	                		        request.getAmount(),
	                		        operationKey,
	                		        "COMPENSATION"
	                		);
	                log.info(
	                        "Compensation successful for sender account: {}",
	                        compensatedAccount
	                );

	                compensationSuccessful = true;

	            } catch (Exception compensationEx) {

	                log.error(
	                        "COMPENSATION FAILED for sender account: {}",
	                        request.getSenderAccountId(),
	                        compensationEx
	                );
	            }
	        }

	        // 16. Set final failure status
	        if (debitSuccessful && !compensationSuccessful) {

	        	changeTransactionStatus(
	        	        transaction,
	        	        TransactionStatus.COMPENSATION_PENDING
	        	);

	        } else {

	        	changeTransactionStatus(
	        	        transaction,
	        	        TransactionStatus.FAILED
	        	);
	        }

	        // 17. Update transaction timestamp
	        transaction.setUpdatedAt(
	                LocalDateTime.now()
	        );

	        // 18. Save final transaction status
	        savedTransaction =
	                transactionRepository.save(transaction);

	        // 19. Keep original exception behavior
	        throw ex;
	    }

	    // 20. Return final transaction response
	    return mapToResponse(savedTransaction);
	}

	@Override
	public TransactionResponse recoverPendingCompensation(Long transactionId) {

	    // 1. Find transaction
	    Transaction transaction = transactionRepository.findById(transactionId)
	            .orElseThrow(() -> new TransactionNotFoundException(
	                    "Transaction not found with id: " + transactionId
	            ));

	    // 2. Verify transaction is waiting for compensation
	    if (transaction.getStatus() != TransactionStatus.COMPENSATION_PENDING) {
	        throw new InvalidTransactionException(
	                "Transaction is not pending compensation"
	        );
	    }

	

	    try {

	        // 3. Create stable idempotency key for compensation
	        String operationKey =
	                transaction.getTransactionReference() + "-COMPENSATION";

	        // 4. Retry compensation
	        AccountResponse compensatedAccount =
	        		accountClient.creditAccount(
	        		        transaction.getSenderAccountId(),
	        		        transaction.getAmount(),
	        		        operationKey,
	        		        "COMPENSATION"
	        		);

	        log.info(
	                "Compensation recovery successful for transaction {}",
	                transaction.getTransactionReference()
	        );

	      

	        // 6. Mark transaction as FAILED
	        changeTransactionStatus(
	                transaction,
	                TransactionStatus.FAILED
	        );

	        transaction.setUpdatedAt(LocalDateTime.now());

	        // 7. Save updated transaction
	        Transaction savedTransaction =
	                transactionRepository.save(transaction);

	        return mapToResponse(savedTransaction);

	    } catch (Exception ex) {

	        // 8. Compensation still failed
	        log.error(
	                "Compensation recovery failed for transaction {}",
	                transaction.getTransactionReference(),
	                ex
	        );

	        // 9. Keep COMPENSATION_PENDING
	        changeTransactionStatus(
	                transaction,
	                TransactionStatus.COMPENSATION_PENDING
	        );
	        transaction.setUpdatedAt(LocalDateTime.now());

	        transactionRepository.save(transaction);

	        throw ex;
	    }
	}
	
	@Override
	public TransactionResponse getTransactionById(Long id) {

		Transaction transaction = transactionRepository.findById(id)
				.orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + id));

		return mapToResponse(transaction);
	}

	@Override
	public TransactionResponse getTransactionByReference(String transactionReference) {

		Transaction transaction = transactionRepository.findByTransactionReference(transactionReference)
				.orElseThrow(() -> new TransactionNotFoundException(
						"Transaction not found with reference: " + transactionReference));

		return mapToResponse(transaction);
	}

	@Override
	public Page<TransactionResponse> getAllTransactions(Pageable pageable) {

	    return transactionRepository
	            .findAll(pageable)
	            .map(this::mapToResponse);
	}
	
	@Override
	public Page<TransactionResponse> getTransactionsBySenderAccountId(
	        Long senderAccountId,
	        Pageable pageable) {

	    return transactionRepository
	            .findBySenderAccountIdOrderByCreatedAtDesc(
	                    senderAccountId,
	                    pageable
	            )
	            .map(this::mapToResponse);
	}
	
	@Override
	public Page<TransactionResponse> getTransactionsByReceiverAccountId(Long receiverAccountId, Pageable pageable) {

	    return transactionRepository
	            .findByReceiverAccountIdOrderByCreatedAtDesc(receiverAccountId, pageable)
	            .map(this::mapToResponse);          
	}
	

	
	@Override
	public Page<TransactionResponse> getTransactionsByStatus(
	        TransactionStatus status, Pageable pageable) {

	    return transactionRepository
	            .findByStatusOrderByCreatedAtDesc(status, pageable)
	            .map(this::mapToResponse);

	}
	
	@Override
	public Page<TransactionResponse> getTransactionsByType(
	        TransactionType transactionType, Pageable pageable) {

	    return transactionRepository
	            .findByTransactionTypeOrderByCreatedAtDesc(transactionType, pageable)
	            .map(this::mapToResponse);
	
	}
	
	@Override
	public Page<TransactionResponse> getTransactionsByDateRange(
	        LocalDateTime from,
	        LocalDateTime to,
	        Pageable pageable
			) {

	    if (from.isAfter(to)) {
	        throw new InvalidDateRangeException(
	                "From date must be before or equal to To date"
	        );
	    }

		
	    return transactionRepository
	            .findByCreatedAtBetweenOrderByCreatedAtDesc(from, to, pageable)
	            .map(this::mapToResponse);
	}
	
	@Override
	public Page<TransactionResponse> getTransactionsByAccountAndDateRange(
	        Long accountId,
	        LocalDateTime from,
	        LocalDateTime to,
	        Pageable pageable) {
		
		if (from.isAfter(to)) {
		    throw new InvalidDateRangeException(
		            "From date must be before or equal to To date"
		    );
		}

	    return transactionRepository
	            .findByAccountIdAndCreatedAtBetween(
	                    accountId,
	                    from,
	                    to,
	                    pageable
	            )
	            .map(this::mapToResponse);
	}
	
	@Override
	public Page<TransactionResponse> getTransactionsByAccountId(
	        Long accountId,
	        Pageable pageable) {

	    return transactionRepository
	            .findByAccountId(accountId, pageable)
	            .map(this::mapToResponse);
	}

	private String generateTransactionReference() {

		long timestamp = System.currentTimeMillis();

		return "TXN-" + timestamp;
	}

	private void changeTransactionStatus(
	        Transaction transaction,
	        TransactionStatus newStatus) {

	    TransactionStatus previousStatus = transaction.getStatus();

	    transactionStatusValidator.validateTransition(
	            previousStatus,
	            newStatus
	    );

	    if (previousStatus == newStatus) {
	        return;
	    }

	    transaction.setStatus(newStatus);

	    TransactionAudit audit = new TransactionAudit();

	    audit.setTransactionId(transaction.getId());
	    audit.setTransactionReference(
	            transaction.getTransactionReference()
	    );
	    audit.setPreviousStatus(previousStatus);
	    audit.setNewStatus(newStatus);
	    audit.setChangedAt(LocalDateTime.now());
	    audit.setReason("Transaction status changed");

	    transactionAuditRepository.save(audit);
	}
	
	@Override
	public Page<TransactionAuditResponse> getTransactionAuditHistory(
	        Long transactionId,
	        int page,
	        int size) {

	    PaginationValidator.validate(page, size);

	    Pageable pageable = PageRequest.of(
	            page,
	            size,
	            Sort.by(Sort.Direction.ASC, "changedAt")
	    );

	    return transactionAuditRepository
	            .findByTransactionIdOrderByChangedAtAsc(
	                    transactionId,
	                    pageable
	            )
	            .map(audit -> {

	                TransactionAuditResponse response =
	                        new TransactionAuditResponse();

	                response.setId(audit.getId());
	                response.setTransactionId(audit.getTransactionId());
	                response.setTransactionReference(
	                        audit.getTransactionReference()
	                );
	                response.setPreviousStatus(
	                        audit.getPreviousStatus()
	                );
	                response.setNewStatus(
	                        audit.getNewStatus()
	                );
	                response.setChangedAt(
	                        audit.getChangedAt()
	                );
	                response.setReason(
	                        audit.getReason()
	                );

	                return response;
	            });
	}
	
	private TransactionResponse mapToResponse(Transaction transaction) {

		TransactionResponse response = new TransactionResponse();

		response.setId(transaction.getId());
		response.setTransactionReference(transaction.getTransactionReference());
		response.setSenderAccountId(transaction.getSenderAccountId());
		response.setReceiverAccountId(transaction.getReceiverAccountId());
		response.setTransactionType(transaction.getTransactionType());
		response.setAmount(transaction.getAmount());
		response.setStatus(transaction.getStatus());
		response.setChannel(transaction.getChannel());
		response.setDescription(transaction.getDescription());
		response.setRemarks(transaction.getRemarks());
		response.setInitiatedBy(transaction.getInitiatedBy());
		response.setCreatedAt(transaction.getCreatedAt());
		response.setUpdatedAt(transaction.getUpdatedAt());

		return response;
	}

   

}
