package com.rafaellbarros.security.exceptions;

import org.springframework.http.HttpStatus;

public class AuthException extends RuntimeException {

    private final HttpStatus statusCode;

    public AuthException(String message, Throwable cause, HttpStatus statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public AuthException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}