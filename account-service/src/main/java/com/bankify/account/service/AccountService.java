package com.bankify.account.service;

import java.util.List;

import com.bankify.account.dto.AccountRequest;
import com.bankify.account.dto.AccountResponse;

public interface AccountService {

	AccountResponse createAccount(AccountRequest request);
	
	AccountResponse getAccountById(Long id);
	
	List<AccountResponse> getAllAccounts();
	
	AccountResponse updateAccount(Long id, AccountRequest request);
	
	void deleteAccount(Long id);
}
