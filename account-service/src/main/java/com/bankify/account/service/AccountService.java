package com.bankify.account.service;

import java.math.BigDecimal;
import java.util.List;

import com.bankify.account.dto.AccountRequest;
import com.bankify.account.dto.AccountResponse;

public interface AccountService {

	AccountResponse createAccount(AccountRequest request);
	
	AccountResponse getAccountById(Long id);
	
	List<AccountResponse> getAllAccounts();
	
	AccountResponse updateAccount(Long id, AccountRequest request);
	
	void deleteAccount(Long id);
	
	AccountResponse debitAccount(Long accountId, BigDecimal amount);
	
	AccountResponse creditAccount(Long accountId, BigDecimal amount);
	
	AccountResponse creditAccount(
	        Long accountId,
	        BigDecimal amount,
	        String operationKey,
	        String operationType
	        );
}
