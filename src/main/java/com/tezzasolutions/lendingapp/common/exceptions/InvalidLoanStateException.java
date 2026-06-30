package com.tezzasolutions.lendingapp.common.exceptions;

public class InvalidLoanStateException extends LendingAppException {
    public InvalidLoanStateException(String message) {
        super(message);
    }
}
