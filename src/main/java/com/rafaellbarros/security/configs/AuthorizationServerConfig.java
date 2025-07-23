package com.rafaellbarros.security.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;

@Configuration
public class AuthorizationServerConfig {

    // Configura um cliente padrão (em memória)
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient client = RegisteredClient
                .withId("1")
                .clientId("client-id")  // Identificador do cliente
                .clientSecret("{noop}client-secret")  // Senha (sem hash para simplificar)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)  // Para serviços
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)  // Para usuários
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)  // Refresh Token
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(1))  // Token expira em 1h
                        .refreshTokenTimeToLive(Duration.ofDays(7))  // Refresh Token expira em 7 dias
                        .build())
                .build();

        return new InMemoryRegisteredClientRepository(client);
    }
}