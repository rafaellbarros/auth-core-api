package com.rafaellbarros.security.services;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.rafaellbarros.security.configs.JwtConfig;
import com.rafaellbarros.security.models.User;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Getter
@Service
public class JwtService {

    private final RSAKey rsaKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final String clientId;

    public JwtService(JwtConfig jwtConfig,
                      @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
                      @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration,
                      @Value("${jwt.client-id}") String clientId)
            throws NoSuchAlgorithmException {
        this.rsaKey = jwtConfig.rsaKey();
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.clientId = clientId;
    }

    public String generateToken(UserDetails userDetails) throws JOSEException {
        User user = (User) userDetails;
        Instant now = Instant.now();

        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(toList());

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer("auth-core-api")
                .subject(userDetails.getUsername())
                .jwtID(UUID.randomUUID().toString()) // ID único para cada token
                .issueTime(Date.from(now))
                .notBeforeTime(Date.from(now)) // Não válido antes de agora
                .expirationTime(Date.from(now.plusSeconds(accessTokenExpiration)))
                .claim("token_type", "Bearer")
                .claim("roles", String.join(", ", authorities)) // Padrão OAuth2
                .claim("client_id", clientId)
                .claim("sid", UUID.randomUUID().toString()) // Session ID
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
                    .map(SimpleGrantedAuthority::new)
                    .collect(toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

}