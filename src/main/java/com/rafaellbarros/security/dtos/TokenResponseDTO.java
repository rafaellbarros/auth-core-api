package com.rafaellbarros.security.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDTO {
    private String access_token;
    private String refresh_token;
    @Builder.Default
    private String token_type = "Bearer"; // Valor padrão
    private long expires_in;
}