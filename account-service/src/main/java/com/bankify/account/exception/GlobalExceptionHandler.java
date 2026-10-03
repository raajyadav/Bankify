package com.bankify.account.exception;

import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<Map<String , Object>> handleDuplicateResource(DuplicateResourceException exception){
	
		Map<String , Object> response = new HashMap<>();
		
		response.put("timestamp", LocalDateTime.now());
		response.put("status", HttpStatus.CONFLICT.value());
		response.put("error", "Duplicate Resource");
		response.put("message", exception.getMessage());
		
		return ResponseEntity
				   .status(HttpStatus.CONFLICT)
				   .body(response);
	}
	
	@ExceptionHandler(AccountNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleAccountNotFound(
	        AccountNotFoundException exception) {

	    Map<String, Object> response = new HashMap<>();

	    response.put("timestamp", LocalDateTime.now());
	    response.put("status", HttpStatus.NOT_FOUND.value());
	    response.put("error", "Account Not Found");
	    response.put("message", exception.getMessage());

	    return ResponseEntity
	            .status(HttpStatus.NOT_FOUND)
	            .body(response);
	}
	
	@ExceptionHandler(AccountNotActiveException.class)
	public ResponseEntity<Map<String, Object>> handleAccountNotActive(
	        AccountNotActiveException exception) {

	    Map<String, Object> response = new HashMap<>();

	    response.put("timestamp", LocalDateTime.now());
	    response.put("status", HttpStatus.BAD_REQUEST.value());
	    response.put("error", "Account Not Active");
	    response.put("message", exception.getMessage());

	    return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body(response);
	}
	
	@ExceptionHandler(InvalidAccountAmountException.class)
	public ResponseEntity<Map<String, Object>> handleInvalidAccountAmount(
	        InvalidAccountAmountException exception) {

	    Map<String, Object> response = new HashMap<>();

	    response.put("timestamp", LocalDateTime.now());
	    response.put("status", HttpStatus.BAD_REQUEST.value());
	    response.put("error", "Invalid Account Amount");
	    response.put("message", exception.getMessage());

	    return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body(response);
	}
	
	@ExceptionHandler(InsufficientBalanceException.class)
	public ResponseEntity<Map<String, Object>> handleInsufficientBalance(
	        InsufficientBalanceException exception) {

	    Map<String, Object> response = new HashMap<>();

	    response.put("timestamp", LocalDateTime.now());
	    response.put("status", HttpStatus.BAD_REQUEST.value());
	    response.put("error", "Insufficient Balance");
	    response.put("message", exception.getMessage());

	    return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body(response);
	}
	
	@ExceptionHandler(DeadlockLoserDataAccessException.class)
	public ResponseEntity<Map<String, Object>> handleDeadlock(
	        DeadlockLoserDataAccessException exception) {

	    Map<String, Object> response = new HashMap<>();

	    response.put("timestamp", LocalDateTime.now());
	    response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
	    response.put("error", "Database Deadlock");
	    response.put(
	            "message",
	            "The operation could not be completed due to a temporary database conflict. Please try again."
	    );

	    return ResponseEntity
	            .status(HttpStatus.SERVICE_UNAVAILABLE)
	            .body(response);
	}
}
