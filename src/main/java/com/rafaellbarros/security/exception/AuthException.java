package com.rafaellbarros.security.exception;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
    
    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}