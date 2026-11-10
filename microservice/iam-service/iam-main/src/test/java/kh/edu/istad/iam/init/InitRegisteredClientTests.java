package kh.edu.istad.iam.init;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InitRegisteredClientTests {

    @Test
    void registersPublicPostmanClientWithPkceAndCallback() {
        RegisteredClientRepository repository = mock(RegisteredClientRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        List<RegisteredClient> savedClients = new ArrayList<>();
        when(repository.findByClientId(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        org.mockito.Mockito.doAnswer(invocation -> {
            savedClients.add(invocation.getArgument(0));
            return null;
        }).when(repository).save(org.mockito.ArgumentMatchers.any(RegisteredClient.class));

        InitRegisteredClient initializer = new InitRegisteredClient(repository, passwordEncoder);
        ReflectionTestUtils.setField(initializer, "registrarClientSecret", "registrar-secret");
        ReflectionTestUtils.setField(initializer, "gatewayClientSecret", "gateway-secret");
        ReflectionTestUtils.setField(initializer, "webClientSecret", "web-secret");

        initializer.run();

        RegisteredClient client = savedClients.stream()
                .filter(saved -> saved.getClientId().equals("postman-client"))
                .findFirst()
                .orElseThrow();
        assertEquals(
                List.of(ClientAuthenticationMethod.NONE),
                List.copyOf(client.getClientAuthenticationMethods())
        );
        assertEquals(
                List.of(AuthorizationGrantType.AUTHORIZATION_CODE),
                List.copyOf(client.getAuthorizationGrantTypes())
        );
        assertEquals(
                List.of("https://oauth.pstmn.io/v1/callback"),
                List.copyOf(client.getRedirectUris())
        );
        assertTrue(client.getClientSettings().isRequireProofKey());
        assertTrue(client.getClientSettings().isRequireAuthorizationConsent());
        assertTrue(client.getScopes().containsAll(List.of(
                OidcScopes.OPENID, OidcScopes.PROFILE, OidcScopes.EMAIL
        )));
    }
}
