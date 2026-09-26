package com.bankify.customer.service;

import com.bankify.customer.dto.CustomerRequest;
import com.bankify.customer.dto.CustomerResponse;

import java.util.List;

public interface CustomerService {

	CustomerResponse createCustomer(CustomerRequest request);

	CustomerResponse getCustomerById(Long id);

	List<CustomerResponse> getAllCustomers();

	CustomerResponse updateCustomer(Long id, CustomerRequest request);

	void deleteCustomer(Long id);
	

}