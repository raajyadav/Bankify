package com.bankify.account.exception;

public class InvalidAccountAmountException extends RuntimeException {

    public InvalidAccountAmountException(String message) {
        super(message);
    }
}