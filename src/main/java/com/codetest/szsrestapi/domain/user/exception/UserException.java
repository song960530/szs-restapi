package com.codetest.szsrestapi.domain.user.exception;

import org.springframework.http.HttpStatus;

public class UserException extends RuntimeException {
    public UserException() {
    }

    public UserException(String message) {
        super(message);
    }
}
