package com.bankify.customer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bankify.customer.dto.CustomerRequest;
import com.bankify.customer.dto.CustomerResponse;
import com.bankify.customer.entity.Customer;
import com.bankify.customer.exception.CustomerNotFoundException;
import com.bankify.customer.exception.DuplicateResourceException;
import com.bankify.customer.repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService{
	

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
    	
        
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        
        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Phone number already exists");
        }

        Customer customer = new Customer();

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setAddress(request.getAddress());

        Customer savedCustomer = customerRepository.save(customer);

        return mapToResponse(savedCustomer);
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFoundException("Customer not found with id: " + id));

        return mapToResponse(customer);
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {

        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {

        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFoundException("Customer not found with id: " + id));

        if(customerRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
        	
        	throw new DuplicateResourceException("Email already exist");
        }
        
        if(customerRepository.existsByPhoneAndIdNot(request.getPhone(), id)) {
        	
        	throw new DuplicateResourceException("Phone number already exist");
        }
        
        existingCustomer.setFirstName(request.getFirstName());
        existingCustomer.setLastName(request.getLastName());
        existingCustomer.setEmail(request.getEmail());
        existingCustomer.setPhone(request.getPhone());
        existingCustomer.setDateOfBirth(request.getDateOfBirth());
        existingCustomer.setAddress(request.getAddress());

        Customer updatedCustomer = customerRepository.save(existingCustomer);

        return mapToResponse(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFoundException("Customer not found with id: " + id));

        customerRepository.delete(customer);
    }

    private CustomerResponse mapToResponse(Customer customer) {

        CustomerResponse response = new CustomerResponse();

        response.setId(customer.getId());
        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setEmail(customer.getEmail());
        response.setPhone(customer.getPhone());
        response.setDateOfBirth(customer.getDateOfBirth());
        response.setAddress(customer.getAddress());
        response.setCreatedAt(customer.getCreatedAt());
        response.setUpdatedAt(customer.getUpdatedAt());

        return response;
    }



}
