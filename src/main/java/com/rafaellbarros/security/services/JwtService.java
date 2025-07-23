package com.rafaellbarros.security.services;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.rafaellbarros.security.configs.JwtConfig;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Service
public class JwtService {

    private final RSAKey rsaKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtService(JwtConfig jwtConfig,
                      @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
                      @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration)
            throws NoSuchAlgorithmException {
        this.rsaKey = jwtConfig.rsaKey();
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateToken(UserDetails userDetails) throws JOSEException {
        Instant now = Instant.now();

        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(userDetails.getUsername())
                .issuer("auth-core-api")
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(accessTokenExpiration)))
                .claim("token_type", "Bearer")
                .claim("authorities", authorities) // Lista de strings simples
                .build();

        return signToken(claims);
    }

    public String generateRefreshToken(UserDetails userDetails) throws JOSEException {
        Instant now = Instant.now();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(userDetails.getUsername())
                .issuer("auth-core-api")
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(refreshTokenExpiration)))
                .claim("tokenType", "refresh")
                .build();

        return signToken(claims);
    }

    private String signToken(JWTClaimsSet claims) throws JOSEException {
        JWSSigner signer = new RSASSASigner(rsaKey.toRSAPrivateKey());
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256)
                        .keyID(rsaKey.getKeyID())
                        .build(),
                claims);

        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());

            // Verifica assinatura e expiração
            return signedJWT.verify(verifier) &&
                    !signedJWT.getJWTClaimsSet().getExpirationTime().before(new Date());
        } catch (JOSEException | ParseException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) throws ParseException {
        return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
    }

    public Collection<? extends GrantedAuthority> getAuthoritiesFromToken(String token) throws ParseException {
        try {
            List<?> authorities = (List<?>) SignedJWT.parse(token)
                    .getJWTClaimsSet()
                    .getClaim("authorities");

            return authorities.stream()
                    .map(Object::toString)
                    .map(auth -> new SimpleGrantedAuthority(auth))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

}