package kh.edu.istad.iam.init;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InitRegisteredClientTests {

    @Test
    void registersPublicOidcAndBffClientsWithPkce() {
        RegisteredClientRepository repository = mock(RegisteredClientRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        List<RegisteredClient> savedClients = new ArrayList<>();
        when(repository.findByClientId(anyString())).thenReturn(null);
        org.mockito.Mockito.doAnswer(invocation -> {
            savedClients.add(invocation.getArgument(0));
            return null;
        }).when(repository).save(org.mockito.ArgumentMatchers.any(RegisteredClient.class));

        InitRegisteredClient initializer = new InitRegisteredClient(passwordEncoder, repository);
        initializer.init();

        assertEquals(2, savedClients.size());
        for (String clientId : List.of("oidc-client", "front-bff-client")) {
            RegisteredClient client = savedClients.stream()
                    .filter(saved -> saved.getClientId().equals(clientId))
                    .findFirst()
                    .orElseThrow();
            assertEquals(
                    Set.of(ClientAuthenticationMethod.NONE),
                    client.getClientAuthenticationMethods()
            );
            assertEquals(
                    Set.of(AuthorizationGrantType.AUTHORIZATION_CODE, AuthorizationGrantType.REFRESH_TOKEN),
                    client.getAuthorizationGrantTypes()
            );
            assertTrue(client.getRedirectUris().stream()
                    .anyMatch(uri -> uri.endsWith("/login/oauth2/code/" + clientId)));
            assertTrue(client.getClientSettings().isRequireProofKey());
            assertTrue(client.getClientSettings().isRequireAuthorizationConsent());
            assertTrue(client.getScopes().containsAll(List.of(OidcScopes.OPENID, OidcScopes.PROFILE)));
        }
    }

    @Test
    void reconcilesExistingBffClientForGatewayLogin() {
        RegisteredClient existingBffClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("front-bff-client")
                .clientSecret("old-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/front-bff-client")
                .build();
        RegisteredClientRepository repository = mock(RegisteredClientRepository.class);
        when(repository.findByClientId("oidc-client")).thenReturn(null);
        when(repository.findByClientId("front-bff-client")).thenReturn(existingBffClient);
        List<RegisteredClient> savedClients = new ArrayList<>();
        org.mockito.Mockito.doAnswer(invocation -> {
            savedClients.add(invocation.getArgument(0));
            return null;
        }).when(repository).save(org.mockito.ArgumentMatchers.any(RegisteredClient.class));

        new InitRegisteredClient(mock(PasswordEncoder.class), repository).init();

        RegisteredClient updatedBffClient = savedClients.stream()
                .filter(client -> client.getClientId().equals("front-bff-client"))
                .findFirst()
                .orElseThrow();
        assertTrue(updatedBffClient.getClientAuthenticationMethods()
                .contains(ClientAuthenticationMethod.NONE));
        assertTrue(updatedBffClient.getAuthorizationGrantTypes()
                .contains(AuthorizationGrantType.AUTHORIZATION_CODE));
        assertTrue(updatedBffClient.getRedirectUris().contains(
                "http://localhost:9999/login/oauth2/code/front-bff-client"));
        assertTrue(updatedBffClient.getRedirectUris().contains(
                "http://127.0.0.1:8080/login/oauth2/code/front-bff-client"));
    }
}
