package com.bankify.transaction.exception;

public class InvalidTransactionStatusTransitionException
        extends RuntimeException {

    public InvalidTransactionStatusTransitionException(String message) {
        super(message);
    }
}