package com.andersen.pc.common.exception;

public class DomainObjectValidationException extends RuntimeException {

    public DomainObjectValidationException(String errorMessage) {
        super(errorMessage);
    }
}
