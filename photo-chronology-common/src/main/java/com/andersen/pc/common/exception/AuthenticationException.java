package com.andersen.pc.common.exception;

public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String errorMessage) {
        super(errorMessage);
    }
}
