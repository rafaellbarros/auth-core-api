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

@RestControllerAdvice
public class AuthExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(AuthExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Tentativa de autenticação com credenciais inválidas: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO("Credenciais inválidas", HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthException(AuthException ex) {
        HttpStatus status = determineStatusFromException(ex);
        log.error("Erro de autenticação: {}", ex.getMessage(), ex);
        return ResponseEntity.status(status)
                .body(new ErrorResponseDTO(ex.getMessage(), status.value()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UserNotFoundException ex) {
        log.warn("Tentativa de autenticação para usuário não encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO("Credenciais inválidas", HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        log.error("Erro interno no servidor: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO("Erro interno no servidor",
                        HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    private HttpStatus determineStatusFromException(AuthException ex) {
        // Se a causa for BadCredentials ou similar, retorna 401
        if (ex.getCause() instanceof BadCredentialsException ||
                ex.getCause() instanceof UsernameNotFoundException) {
            return HttpStatus.UNAUTHORIZED;
        }
        // Caso contrário, retorna 500
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}