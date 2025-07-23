package com.rafaellbarros.security.exception;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(String message) {
        super(message);
    }
    
    public BadCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}