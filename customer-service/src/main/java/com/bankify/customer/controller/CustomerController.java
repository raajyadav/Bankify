package com.bankify.customer.controller;

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

import com.bankify.customer.dto.CustomerRequest;
import com.bankify.customer.dto.CustomerResponse;
import com.bankify.customer.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	private final CustomerService customerService;

	CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@PostMapping
	public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {

		CustomerResponse response = customerService.createCustomer(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {

		CustomerResponse response = customerService.getCustomerById(id);

		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<List<CustomerResponse>> getAllCustomers() {

		return ResponseEntity.ok(customerService.getAllCustomers());
	}

	@PutMapping("/{id}")
	public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Long id,
			@Valid @RequestBody CustomerRequest request) {

		CustomerResponse response = customerService.updateCustomer(id, request);

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<CustomerResponse> delete(@PathVariable Long id) {

		customerService.deleteCustomer(id);

		return ResponseEntity.noContent().build();
	}

}
