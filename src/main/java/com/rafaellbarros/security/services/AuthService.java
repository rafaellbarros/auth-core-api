package com.rafaellbarros.security.services;

import com.nimbusds.jose.JOSEException;
import com.rafaellbarros.security.dtos.LoginRequestDTO;
import com.rafaellbarros.security.dtos.TokenResponseDTO;
import com.rafaellbarros.security.exceptions.AuthException;
import com.rafaellbarros.security.exceptions.BadCredentialsException;
import com.rafaellbarros.security.exceptions.InvalidTokenException;
import com.rafaellbarros.security.exceptions.UserNotFoundException;
import com.rafaellbarros.security.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public TokenResponseDTO authenticate(LoginRequestDTO request) {
        try {

            User user = (User) userService.loadUserByUsername(request.getUsername());

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

            return TokenResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getAccessTokenExpiration())
                    .build();

        } catch (UsernameNotFoundException e) {
            log.warn("Usuário não encontrado: {}", request.getUsername());
            throw new AuthException("Credenciais inválidas", e, HttpStatus.UNAUTHORIZED);
        } catch (BadCredentialsException e) {
            log.warn("Senha incorreta para usuário: {}", request.getUsername());
            throw new AuthException("Credenciais inválidas", e, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Erro durante autenticação para usuário {}: {}", request.getUsername(), e.getMessage(), e);
            throw new AuthException("Falha na autenticação", e, HttpStatus.INTERNAL_SERVER_ERROR);
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
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getAccessTokenExpiration())
                    .build();

        } catch (ParseException e) {
            throw new InvalidTokenException("Token malformado");
        } catch (UsernameNotFoundException e) {
            throw new UserNotFoundException("Usuário não encontrado");
        } catch (JOSEException e) {
            throw new AuthException("Falha ao renovar token", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}