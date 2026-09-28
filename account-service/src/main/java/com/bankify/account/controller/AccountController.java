package com.bankify.account.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankify.account.dto.AccountRequest;
import com.bankify.account.dto.AccountResponse;
import com.bankify.account.service.AccountService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;

	@PostMapping
	public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request) {

		AccountResponse response = accountService.createAccount(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long id) {

		AccountResponse response = accountService.getAccountById(id);

		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<List<AccountResponse>> getAllAccounts() {

		List<AccountResponse> response = accountService.getAllAccounts();

		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<AccountResponse> updateAccount(@PathVariable Long id,
			@Valid @RequestBody AccountRequest request) {

		AccountResponse response = accountService.updateAccount(id, request);

		return ResponseEntity.ok(response);

	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {

		accountService.deleteAccount(id);

		return ResponseEntity.noContent().build();
	}

}
