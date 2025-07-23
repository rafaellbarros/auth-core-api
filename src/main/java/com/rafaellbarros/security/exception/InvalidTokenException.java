package com.rafaellbarros.security.exception;

public class InvalidTokenException extends BadCredentialsException {

    public InvalidTokenException(String message) {
        super(message);
    }
    
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}