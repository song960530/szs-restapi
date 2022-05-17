package com.codetest.szsrestapi.global.error;

public class CipherException extends RuntimeException{
    public CipherException() {
    }

    public CipherException(String message) {
        super(message);
    }
}
