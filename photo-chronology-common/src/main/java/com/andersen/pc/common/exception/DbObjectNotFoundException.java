package com.andersen.pc.common.exception;

public class DbObjectNotFoundException extends RuntimeException {

    public DbObjectNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
