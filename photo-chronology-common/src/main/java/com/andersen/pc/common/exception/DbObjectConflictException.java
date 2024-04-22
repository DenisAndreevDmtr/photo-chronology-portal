package com.andersen.pc.common.exception;

public class DbObjectConflictException extends RuntimeException {

    public DbObjectConflictException(String errorMessage) {
        super(errorMessage);
    }
}
