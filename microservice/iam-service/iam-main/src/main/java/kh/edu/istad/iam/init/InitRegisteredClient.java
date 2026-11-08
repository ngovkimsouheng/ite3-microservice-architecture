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
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS);

        RegisteredClient registrarClient = builder
                .clientSecret(passwordEncoder.encode(registrarClientSecret))
                .scope("client.create")
                .scope("client.read")
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
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS);

        RegisteredClient gatewayClient = builder
                .clientSecret(passwordEncoder.encode(gatewayClientSecret))
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
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN);

        RegisteredClient webClient = builder
                .clientSecret(passwordEncoder.encode(webClientSecret))
                // Section E.3 & E.4 Web Redirects
                .redirectUri("http://localhost:8081/login/oauth2/code/iam")
                .redirectUri("https://oauth.pstmn.io/v1/callback") // Postman test callback
                .redirectUri("http://127.0.0.1:9000/login/oauth2/code/iam")
                // Scopes per Section E.2
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.EMAIL)
                .scope(OidcScopes.PHONE)
                .scope(OidcScopes.ADDRESS)
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .requireProofKey(false) // Set to false if Postman tests without PKCE; true for production
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