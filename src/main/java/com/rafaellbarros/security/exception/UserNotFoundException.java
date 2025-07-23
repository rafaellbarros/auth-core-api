package com.rafaellbarros.security.exception;

public class UserNotFoundException extends BadCredentialsException {
    public UserNotFoundException(String message) {
        super(message);
    }
    
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}