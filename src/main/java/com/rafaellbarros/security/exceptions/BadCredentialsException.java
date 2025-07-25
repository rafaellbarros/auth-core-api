package com.rafaellbarros.security.exceptions;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(String message) {
        super(message);
    }
    
    public BadCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}