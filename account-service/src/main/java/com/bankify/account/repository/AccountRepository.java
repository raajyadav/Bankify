package com.bankify.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankify.account.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long>{

	Optional<Account> findByAccountNumber(String accountNumber);
	
	boolean existsByAccountNumber(String accountNumber);
	
	boolean existsByCustomerId(Long customerId);
}
