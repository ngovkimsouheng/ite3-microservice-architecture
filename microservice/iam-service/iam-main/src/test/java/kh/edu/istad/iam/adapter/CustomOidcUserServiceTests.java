package kh.edu.istad.iam.adapter;

import kh.edu.istad.iam.domain.dto.AuthUserResult;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class CustomOidcUserServiceTests {

    @Test
    void createsJacksonSupportedPrincipalUsingIamIdentityAndPreservesProviderClaims() {
        UUID userUuid = UUID.randomUUID();
        OidcIdToken idToken = new OidcIdToken(
                "provider-token",
                Instant.now(),
                Instant.now().plusSeconds(300),
                Map.of("sub", "provider-subject", "iss", "https://issuer.example")
        );
        OidcUser providerUser = new DefaultOidcUser(
                Set.of(new SimpleGrantedAuthority("ROLE_USER")),
                idToken,
                new OidcUserInfo(Map.of("sub", "provider-subject", "email", "user@example.com"))
        );
        AuthUserResult authUser = new AuthUserResult(
                userUuid, "user", "user@example.com", "", true, true, true, true, Set.of("ROLE_USER")
        );

        OidcUser iamUser = CustomOidcUserService.withIamIdentity(providerUser, authUser);

        assertInstanceOf(DefaultOidcUser.class, iamUser);
        assertEquals(userUuid.toString(), iamUser.getName());
        assertEquals("provider-subject", iamUser.getClaim("sub"));
        assertEquals("https://issuer.example", iamUser.getClaim("iss"));
        assertEquals("user@example.com", iamUser.getClaim("email"));
        assertEquals(Set.of("ROLE_USER"), iamUser.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(java.util.stream.Collectors.toSet()));
    }
}
