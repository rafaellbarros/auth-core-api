package com.rafaellbarros.security.exceptions.handlers;

import com.rafaellbarros.security.dtos.ErrorResponseDTO;
import com.rafaellbarros.security.exceptions.AuthException;
import com.rafaellbarros.security.exceptions.BadCredentialsException;
import com.rafaellbarros.security.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class AuthenticationExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        String path = getRequestPath(request);
        log.warn("Tentativa de autenticação com credenciais inválidas - Path: {} - Message: {}", path, ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciais inválidas",
                        "Falha na autenticação: " + ex.getMessage(),
                        path
                ));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        String path = getRequestPath(request);
        log.warn("Tentativa de autenticação para usuário não encontrado - Path: {} - Message: {}", path, ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciais inválidas",
                        "Usuário não encontrado: " + ex.getMessage(),
                        path
                ));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthException(AuthException ex, WebRequest request) {
        String path = getRequestPath(request);
        HttpStatus status = ex.getStatusCode();

        log.error("Erro de autenticação - Path: {} - Status: {} - Message: {}", path, status, ex.getMessage(), ex);

        return ResponseEntity.status(status)
                .body(new ErrorResponseDTO(
                        status,
                        "Falha na autenticação",
                        ex.getMessage(),
                        path
                ));
    }

    private String getRequestPath(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            return ((ServletWebRequest) request).getRequest().getRequestURI();
        }
        return request.getDescription(false).replace("uri=", "");
    }
}