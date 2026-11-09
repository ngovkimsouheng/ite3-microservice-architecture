package kh.edu.istad.iam;

import kh.edu.istad.iam.config.IamJwtAuthenticationConverter;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class IamJwtAuthenticationConverterTests {

    @Test
    void mapsScopesAndIamAuthorityClaimsToApiAuthorities() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("scope", "user:create user:read")
                .claim("roles", List.of("ROLE_ADMIN"))
                .claim("permissions", List.of("user:delete"))
                .build();

        var authentication = new IamJwtAuthenticationConverter().convert(jwt);
        Set<String> authorities = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toSet());

        assertTrue(authorities.contains("SCOPE_user:create"));
        assertTrue(authorities.contains("user:create"));
        assertTrue(authorities.contains("user:read"));
        assertTrue(authorities.contains("ROLE_ADMIN"));
        assertTrue(authorities.contains("user:delete"));
    }
}
