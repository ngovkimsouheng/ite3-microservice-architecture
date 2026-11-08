package kh.edu.istad.iam.init;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InitRegisteredClient implements CommandLineRunner {

    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.oauth2.clients.registrar.secret:${REGISTRAR_CLIENT_SECRET:registrar-secret}}")
    private String registrarClientSecret;

    @Value("${app.oauth2.clients.gateway.secret:${GATEWAY_CLIENT_SECRET:gateway-secret}}")
    private String gatewayClientSecret;

    @Value("${app.oauth2.clients.web.secret:${WEB_CLIENT_SECRET:web-secret}}")
    private String webClientSecret;

    @Override
    public void run(String... args) {
        initRegistrarClient();
        initGatewayClient();
        initWebClient();
    }

    private void initRegistrarClient() {
        RegisteredClient existing = registeredClientRepository.findByClientId("registrar-client");
        RegisteredClient.Builder builder = (existing != null)
                ? RegisteredClient.from(existing)
                : RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("registrar-client")
                .clientSecret(passwordEncoder.encode(registrarClientSecret))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS);

        RegisteredClient registrarClient = builder
                .scope("client.create")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(2))
                        .build())
                .build();

        registeredClientRepository.save(registrarClient);
    }

    private void initGatewayClient() {
        RegisteredClient existing = registeredClientRepository.findByClientId("gateway-client");
        RegisteredClient.Builder builder = (existing != null)
                ? RegisteredClient.from(existing)
                : RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("gateway-client")
                .clientSecret(passwordEncoder.encode(gatewayClientSecret))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS);

        RegisteredClient gatewayClient = builder
                // User scopes
                .scope("user:read")
                .scope("user:create")
                .scope("user:update")
                .scope("user:delete")
                .scope("user:assign")
                // Role scopes
                .scope("role:read")
                .scope("role:create")
                .scope("role:update")
                .scope("role:delete")
                .scope("role:assign")
                // Permission scopes
                .scope("permission:read")
                .scope("permission:create")
                .scope("permission:update")
                .scope("permission:delete")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(4))
                        .build())
                .build();

        registeredClientRepository.save(gatewayClient);
    }

    private void initWebClient() {
        RegisteredClient existing = registeredClientRepository.findByClientId("web-client");
        RegisteredClient.Builder builder = (existing != null)
                ? RegisteredClient.from(existing)
                : RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("web-client")
                .clientSecret(passwordEncoder.encode(webClientSecret))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN);

        RegisteredClient webClient = builder
                .redirectUri("http://localhost:8081/login/oauth2/code/iam")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.EMAIL)
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(30))
                        .refreshTokenTimeToLive(Duration.ofDays(7))
                        .reuseRefreshTokens(true)
                        .build())
                .build();

        registeredClientRepository.save(webClient);
    }
}

/*
package kh.edu.istad.iam.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InitRegisteredClient implements CommandLineRunner {

    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1. Section E.3: Initial token provider client with scope client.create
        if (registeredClientRepository.findByClientId("registrar-client") == null) {
            RegisteredClient registrarClient = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId("registrar-client")
                    .clientSecret(passwordEncoder.encode("registrar-secret"))
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .scope("client.create")
                    .tokenSettings(TokenSettings.builder()
                            .accessTokenTimeToLive(Duration.ofHours(2))
                            .build())
                    .build();
            registeredClientRepository.save(registrarClient);
        }

        // 2. Internal Client for Testing APIs (client_credentials)
        if (registeredClientRepository.findByClientId("gateway-client") == null) {
            RegisteredClient gatewayClient = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId("gateway-client")
                    .clientSecret(passwordEncoder.encode("gateway-secret"))
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .scope("user:read")
                    .scope("user:create")
                    .scope("user:update")
                    .scope("user:delete")
                    .scope("role:read")
                    .scope("permission:read")
                    .scope("permission:create")
                    .scope("permission:update")
                    .scope("permission:delete")
                    .tokenSettings(TokenSettings.builder()
                            .accessTokenTimeToLive(Duration.ofHours(4))
                            .build())
                    .build();
            registeredClientRepository.save(gatewayClient);
        }

        // 3. Authorization Code Client (For Section F JTE & Frontend)
        if (registeredClientRepository.findByClientId("web-client") == null) {
            RegisteredClient webClient = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId("web-client")
                    .clientSecret(passwordEncoder.encode("web-secret"))
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .redirectUri("http://localhost:8081/login/oauth2/code/iam")
                    .scope(OidcScopes.OPENID)
                    .scope(OidcScopes.PROFILE)
                    .scope(OidcScopes.EMAIL)
                    .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                    .tokenSettings(TokenSettings.builder()
                            .accessTokenTimeToLive(Duration.ofMinutes(30))
                            .refreshTokenTimeToLive(Duration.ofDays(7))
                            .build())
                    .build();
            registeredClientRepository.save(webClient);
        }
    }
}*/
