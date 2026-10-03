package com.bankify.transaction.exception;

public class TransactionNotFoundException extends RuntimeException{

	public TransactionNotFoundException(String message) {
	      
		super(message);
	}
}
