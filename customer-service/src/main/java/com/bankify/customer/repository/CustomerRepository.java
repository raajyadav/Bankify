package com.bankify.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankify.customer.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>{

	boolean existsByEmail(String email);

	boolean existsByPhone(String phone);
	
	boolean existsByEmailAndIdNot(String email, Long id);

	boolean existsByPhoneAndIdNot(String phone, Long id);
}
