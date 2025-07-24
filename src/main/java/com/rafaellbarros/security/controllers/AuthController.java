package com.rafaellbarros.security.controllers;

import com.rafaellbarros.security.dtos.LoginRequestDTO;
import com.rafaellbarros.security.dtos.TokenResponseDTO;
import com.rafaellbarros.security.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        TokenResponseDTO tokenResponseDTO = authService.authenticate(request);
        return ResponseEntity.ok(tokenResponseDTO);
    }
}
