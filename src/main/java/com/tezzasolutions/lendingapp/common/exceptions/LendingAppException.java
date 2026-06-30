package com.tezzasolutions.lendingapp.common.exceptions;

public class LendingAppException extends RuntimeException {
    public LendingAppException(String message) {
        super(message);
    }

    public LendingAppException(String message, Throwable cause) {
        super(message, cause);
    }
}
