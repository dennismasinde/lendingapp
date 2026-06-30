package com.tezzasolutions.lendingapp.common.exceptions;

public class ResourceNotFoundException extends LendingAppException {
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));
    }
}
