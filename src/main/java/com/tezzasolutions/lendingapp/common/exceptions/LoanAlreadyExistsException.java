package com.tezzasolutions.lendingapp.common.exceptions;

public class LoanAlreadyExistsException extends LendingAppException {
    public LoanAlreadyExistsException(String message) {
        super(message);
    }
}
