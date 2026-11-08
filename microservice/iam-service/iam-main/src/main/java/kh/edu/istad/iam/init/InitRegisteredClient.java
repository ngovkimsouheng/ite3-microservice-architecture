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
}