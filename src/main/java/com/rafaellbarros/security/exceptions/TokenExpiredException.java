package com.rafaellbarros.security.exceptions;

public class TokenExpiredException extends BadCredentialsException {

    public TokenExpiredException(String message) {
        super(message);
    }
    
    public TokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

}