package com.rafaellbarros.security.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO {

    private String message;
    private int statusCode;
    private String errorCode;
    private LocalDateTime timestamp;
    
    public ErrorResponseDTO(String message, int statusCode) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.statusCode = statusCode;
    }
}