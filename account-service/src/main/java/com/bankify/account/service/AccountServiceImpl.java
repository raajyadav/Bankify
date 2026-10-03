package com.bankify.account.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bankify.account.dto.AccountRequest;
import com.bankify.account.dto.AccountResponse;
import com.bankify.account.entity.Account;
import com.bankify.account.entity.AccountOperation;
import com.bankify.account.exception.AccountNotActiveException;
import com.bankify.account.exception.AccountNotFoundException;
import com.bankify.account.exception.DuplicateResourceException;
import com.bankify.account.exception.InsufficientBalanceException;
import com.bankify.account.exception.InvalidAccountAmountException;
import com.bankify.account.repository.AccountOperationRepository;
import com.bankify.account.repository.AccountRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{
	
	private final AccountRepository accountRepository;
	
	private final AccountOperationRepository accountOperationRepository;
	
	private final AccountTransactionService accountTransactionService;
	
	@Value("${bankify.test.credit-failure:false}")
	private boolean testCreditFailure;

	@Override
	public AccountResponse createAccount(AccountRequest request) {

	    if (accountRepository.existsByCustomerId(request.getCustomerId())) {
	        throw new DuplicateResourceException(
	                "Customer already has an account"
	        );
	    }

	    Account account = new Account();

	    account.setAccountNumber(generateAccountNumber());
	    account.setAccountType(request.getAccountType());
	    account.setBalance(request.getBalance());
	    account.setStatus("ACTIVE");
	    account.setCustomerId(request.getCustomerId());

	    LocalDateTime now = LocalDateTime.now();

	    account.setCreatedAt(now);
	    account.setUpdatedAt(now);

	    Account savedAccount = accountRepository.save(account);

	    return mapToResponse(savedAccount);
	}

	@Override
	public AccountResponse getAccountById(Long id) {

	    Account account = accountRepository.findById(id)
	            .orElseThrow(() ->
	                    new AccountNotFoundException(
	                            "Account not found with id: " + id
	                    )
	            );

	    return mapToResponse(account);
	}

	@Override
	public List<AccountResponse> getAllAccounts() {
		
		return accountRepository.findAll()
				  .stream()
				  .map(this::mapToResponse)
				  .toList();
	}

	@Override
	public AccountResponse updateAccount(Long id, AccountRequest request) {
		
		Account existingAccount =  accountRepository.findById(id)
				.orElseThrow(() ->
		                   new AccountNotFoundException(
		                	"Account not found with id: "+ id	   
		                  ) 
		         ); 
		
		existingAccount.setAccountType(request.getAccountType());
		existingAccount.setBalance(request.getBalance());
		existingAccount.setUpdatedAt(LocalDateTime.now());
		
		Account updatedAccount = accountRepository.save(existingAccount);
		
		return mapToResponse(updatedAccount);
	}

	@Override
	public void deleteAccount(Long id) {
		
	  Account existingAccount = accountRepository.findById(id)
			   .orElseThrow(()->
			        new AccountNotFoundException(
			     		"Account not found with id: " +id  
			        )
			);
		      
	  accountRepository.delete(existingAccount);
		
	}
	
	@Override
	public AccountResponse debitAccount(Long accountId, BigDecimal amount) {

	    accountTransactionService.debitAccount(accountId, amount);

	    Account account = accountRepository.findById(accountId)
	            .orElseThrow(() ->
	                    new AccountNotFoundException(
	                            "Account not found with id: " + accountId
	                    )
	            );

	    return mapToResponse(account);
	}
	
	@Override
	@Transactional
	public AccountResponse creditAccount(Long accountId, BigDecimal amount) {
		
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
	    
	 // TEMPORARY TEST CODE - REMOVE AFTER TESTING
	    if (testCreditFailure) {
	        throw new RuntimeException(
	                "Temporary credit failure for compensation testing"
	        );
	    }

	    if (amount == null ||
	            amount.compareTo(BigDecimal.ZERO) <= 0) {

	        throw new InvalidAccountAmountException(
	                "Amount must be greater than zero"
	        );
	    }
	    

	    BigDecimal newBalance =
	            account.getBalance().add(amount);

	    account.setBalance(newBalance);

	    account.setUpdatedAt(LocalDateTime.now());

	    Account updatedAccount =
	            accountRepository.save(account);

	    return mapToResponse(updatedAccount);
	}
	
	@Override
	public AccountResponse creditAccount(
	        Long accountId,
	        BigDecimal amount,
	        String operationKey,
	        String operationType) {

	    accountTransactionService.creditAccount(
	            accountId,
	            amount,
	            operationKey,
	            operationType
	    );

	    Account account = accountRepository.findById(accountId)
	            .orElseThrow(() -> new AccountNotFoundException(
	                    "Account not found with id: " + accountId));

	    return mapToResponse(account);
	}
	
	private String generateAccountNumber() {
		
		long number = System.currentTimeMillis();
		
		return "BK" + number;
	}
	
	private AccountResponse mapToResponse(Account account) {

	    AccountResponse response = new AccountResponse();

	    response.setId(account.getId());
	    response.setAccountNumber(account.getAccountNumber());
	    response.setAccountType(account.getAccountType());
	    response.setBalance(account.getBalance());
	    response.setStatus(account.getStatus());
	    response.setCustomerId(account.getCustomerId());
	    response.setCreatedAt(account.getCreatedAt());
	    response.setUpdatedAt(account.getUpdatedAt());

	    return response;
	}

	
}
