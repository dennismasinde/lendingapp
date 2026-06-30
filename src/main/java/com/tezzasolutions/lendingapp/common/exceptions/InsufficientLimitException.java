package com.tezzasolutions.lendingapp.common.exceptions;

public class InsufficientLimitException extends LendingAppException {
    public InsufficientLimitException(String message) {
        super(message);
    }
}
