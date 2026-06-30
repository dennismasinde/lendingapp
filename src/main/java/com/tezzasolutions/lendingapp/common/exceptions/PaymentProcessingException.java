package com.tezzasolutions.lendingapp.common.exceptions;

public class PaymentProcessingException extends LendingAppException {
    public PaymentProcessingException(String message) {
        super(message);
    }

    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
