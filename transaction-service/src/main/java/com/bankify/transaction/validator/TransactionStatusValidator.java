package com.bankify.transaction.validator;

import org.springframework.stereotype.Component;

import com.bankify.transaction.enums.TransactionStatus;
import com.bankify.transaction.exception.InvalidTransactionStatusTransitionException;

@Component
public class TransactionStatusValidator {

    public void validateTransition(
            TransactionStatus currentStatus,
            TransactionStatus newStatus) {

        if (currentStatus == null || newStatus == null) {
            throw new InvalidTransactionStatusTransitionException(
                    "Transaction status cannot be null"
            );
        }

        if (currentStatus == newStatus) {
            return;
        }

        boolean validTransition = switch (currentStatus) {

            case PENDING ->
                    newStatus == TransactionStatus.SUCCESS
                    || newStatus == TransactionStatus.FAILED
                    || newStatus == TransactionStatus.COMPENSATION_PENDING;

            case COMPENSATION_PENDING ->
                    newStatus == TransactionStatus.FAILED;

            case SUCCESS, FAILED ->
                    false;
        };

        if (!validTransition) {
            throw new InvalidTransactionStatusTransitionException(
                    "Invalid transaction status transition: "
                    + currentStatus + " -> " + newStatus
            );
        }
    }
}