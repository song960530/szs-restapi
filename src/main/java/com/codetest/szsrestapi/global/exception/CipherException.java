package com.codetest.szsrestapi.global.exception;

public class CipherException extends RuntimeException{
    public CipherException() {
    }

    public CipherException(String message) {
        super(message);
    }
}
