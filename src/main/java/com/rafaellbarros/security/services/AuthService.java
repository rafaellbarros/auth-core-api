package com.rafaellbarros.security.services;

import com.nimbusds.jose.JOSEException;
import com.rafaellbarros.security.dtos.LoginRequestDTO;
import com.rafaellbarros.security.dtos.TokenResponseDTO;
import com.rafaellbarros.security.exception.AuthException;
import com.rafaellbarros.security.exception.BadCredentialsException;
import com.rafaellbarros.security.exception.InvalidTokenException;
import com.rafaellbarros.security.exception.UserNotFoundException;
import com.rafaellbarros.security.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
@RequiredArgsConstructor
@Slf4j // Para logging
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public TokenResponseDTO authenticate(LoginRequestDTO request) {
        try {
            log.info("Tentativa de login para usuário: {}", request.getUsername());

            User user = (User) userService.loadUserByUsername(request.getUsername());
            log.info("Usuário encontrado: {}", user.getUsername());

            log.info("Senha fornecida: {}", request.getPassword());
            log.info("Senha armazenada: {}", user.getPassword());

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("Senha não corresponde para usuário: {}", request.getUsername());
                throw new BadCredentialsException("Credenciais inválidas");
            }

            if (!user.isEnabled()) {
                log.warn("Usuário desativado: {}", request.getUsername());
                throw new DisabledException("Conta desativada");
            }

            String accessToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            log.info("Token gerado para usuário: {}", request.getUsername());

            return TokenResponseDTO.builder()
                    .access_token(accessToken)
                    .refresh_token(refreshToken)
                    .token_type("Bearer")
                    .expires_in(jwtService.getAccessTokenExpiration())
                    .build();

        } catch (UsernameNotFoundException e) {
            log.error("Usuário não encontrado: {}", request.getUsername());
            throw new UserNotFoundException("Usuário não encontrado");
        } catch (Exception e) {
            log.error("Erro durante autenticação: {}", e.getMessage());
            throw new AuthException("Falha na autenticação", e);
        }
    }

    public TokenResponseDTO refreshToken(String refreshToken) {
        try {
            if (!jwtService.validateToken(refreshToken)) {
                throw new InvalidTokenException("Refresh token inválido");
            }

            String username = jwtService.getUsernameFromToken(refreshToken);
            UserDetails userDetails = userService.loadUserByUsername(username);

            String newAccessToken = jwtService.generateToken(userDetails);
            String newRefreshToken = jwtService.generateRefreshToken(userDetails);

            return TokenResponseDTO.builder()
                    .access_token(newAccessToken)
                    .refresh_token(newRefreshToken)
                    .token_type("Bearer")
                    .expires_in(jwtService.getAccessTokenExpiration())
                    .build();

        } catch (ParseException e) {
            throw new InvalidTokenException("Token malformado");
        } catch (UsernameNotFoundException e) {
            throw new UserNotFoundException("Usuário não encontrado");
        } catch (JOSEException e) {
            throw new AuthException("Falha ao renovar token", e);
        }
    }
}