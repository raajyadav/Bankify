package com.bankify.account.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bankify.account.dto.AccountRequest;
import com.bankify.account.dto.AccountResponse;
import com.bankify.account.entity.Account;
import com.bankify.account.exception.AccountNotFoundException;
import com.bankify.account.exception.DuplicateResourceException;
import com.bankify.account.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{
	
	private final AccountRepository accountRepository;

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
