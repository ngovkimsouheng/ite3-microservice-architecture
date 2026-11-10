package kh.edu.istad.iam.init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.util.UUID;

@RequiredArgsConstructor
@Configuration
public class InitRegisteredClient {

    private static final String GATEWAY_REDIRECT_URI =
            "http://localhost:9999/login/oauth2/code/front-bff-client";

    private final PasswordEncoder passwordEncoder;
    private final RegisteredClientRepository registeredClientRepository;

    @PostConstruct
    public void init() {
        TokenSettings tokenSettings = TokenSettings.builder()
                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                .refreshTokenTimeToLive(Duration.ofDays(7))
                .accessTokenTimeToLive(Duration.ofHours(5))
                .authorizationCodeTimeToLive(Duration.ofMinutes(3))
                .reuseRefreshTokens(false)
                .build();

        ClientSettings clientSettings = ClientSettings.builder()
                .requireProofKey(true)
                .requireAuthorizationConsent(true)
                .build();

        RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("oidc-client")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/oidc-client")
                .postLogoutRedirectUri("http://127.0.0.1:8080/")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(clientSettings)
                .tokenSettings(tokenSettings)
                .build();

        RegisteredClient frontBffClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("front-bff-client")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri(GATEWAY_REDIRECT_URI)
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/front-bff-client")
                .postLogoutRedirectUri("http://localhost:9999")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(clientSettings)
                .tokenSettings(tokenSettings)
                .build();
        RegisteredClient customerClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("customer-client")
                .clientSecret(passwordEncoder.encode("customer-secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("customer:read")
                .tokenSettings(tokenSettings)
                .build();
        if (registeredClientRepository.findByClientId("customer-client") == null) {
            registeredClientRepository.save(customerClient);
        }
        if (registeredClientRepository.findByClientId("oidc-client") == null) {
            registeredClientRepository.save(oidcClient);
        }
        RegisteredClient existingCustomerClient =
                registeredClientRepository.findByClientId("customer-client");
        if (existingCustomerClient == null) {
            registeredClientRepository.save(customerClient);
        } else {
            registeredClientRepository.save(RegisteredClient.from(existingCustomerClient)
                    .clientSecret(passwordEncoder.encode("customer-secret"))
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .scope(OidcScopes.OPENID)
                    .scope(OidcScopes.PROFILE)
                    .scope("customer:read")
                    .tokenSettings(tokenSettings)
                    .build());
        }
        RegisteredClient existingFrontBffClient =
                registeredClientRepository.findByClientId("front-bff-client");
        if (existingFrontBffClient == null) {
            registeredClientRepository.save(frontBffClient);
        } else {
            registeredClientRepository.save(RegisteredClient.from(existingFrontBffClient)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .redirectUri(GATEWAY_REDIRECT_URI)
                    .redirectUri("http://127.0.0.1:8080/login/oauth2/code/front-bff-client")
                    .postLogoutRedirectUri("http://localhost:9999")
                    .scope(OidcScopes.OPENID)
                    .scope(OidcScopes.PROFILE)
                    .clientSettings(clientSettings)
                    .tokenSettings(tokenSettings)
                    .build());
        }
    }
}
