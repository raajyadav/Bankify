package com.bankify.account.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
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
}
