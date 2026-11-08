package kh.edu.istad.iam.config;

import kh.edu.istad.iam.application.dto.CommonDtos.UserAuthoritiesResult;
import kh.edu.istad.iam.application.dto.CommonDtos.UserResult;
import kh.edu.istad.iam.application.usecase.user.GetUserAuthoritiesUseCase;
import kh.edu.istad.iam.application.usecase.user.GetUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.Set;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class JwtCustomizerConfig {

    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    private final GetUserUseCase getUserUseCase;

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return context -> {
            String principalName = context.getPrincipal().getName();

            UUID userUuid;
            try {
                userUuid = UUID.fromString(principalName);
            } catch (IllegalArgumentException e) {
                // Client credentials flow or non-UUID principal
                return;
            }

            // Always set subject to users.uuid
            context.getClaims().subject(userUuid.toString());

            // Access Token: roles and permissions (Section E.1 & E.2)
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                UserAuthoritiesResult authResult = getUserAuthoritiesUseCase.execute(userUuid);
                if (authResult != null) {
                    context.getClaims().claim("roles", authResult.roles());
                    context.getClaims().claim("permissions", authResult.permissions());
                }
            }

            // ID Token: OIDC Claims (Section E.2)
            if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
                Set<String> authorizedScopes = context.getAuthorizedScopes();
                UserResult user = getUserUseCase.execute(userUuid);
                if (user != null) {
                    if (authorizedScopes.contains(OidcScopes.PROFILE)) {
                        context.getClaims().claim("name", ((user.givenName() != null ? user.givenName() : "") + " " + (user.familyName() != null ? user.familyName() : "")).trim());
                        context.getClaims().claim("given_name", user.givenName());
                        context.getClaims().claim("family_name", user.familyName());
                        context.getClaims().claim("preferred_username", user.username());
                        if (user.locale() != null) context.getClaims().claim("locale", user.locale());
                        if (user.updatedAt() != null) {
                            context.getClaims().claim("updated_at", user.updatedAt().toEpochSecond());
                        }
                    }
                    if (authorizedScopes.contains(OidcScopes.EMAIL)) {
                        context.getClaims().claim("email", user.email());
                        context.getClaims().claim("email_verified", user.emailVerified());
                    }
                    if (authorizedScopes.contains(OidcScopes.PHONE) && user.phoneNumber() != null) {
                        context.getClaims().claim("phone_number", user.phoneNumber());
                    }
                }
            }
        };
    }
}