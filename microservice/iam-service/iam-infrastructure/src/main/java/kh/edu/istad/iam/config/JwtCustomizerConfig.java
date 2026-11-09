package kh.edu.istad.iam.config;

import kh.edu.istad.iam.domain.dto.UserAuthoritiesResult;
import kh.edu.istad.iam.domain.dto.AuthUserResult;
import kh.edu.istad.iam.domain.dto.UserResult;
import kh.edu.istad.iam.domain.usecase.user.GetUserAuthoritiesUseCase;
import kh.edu.istad.iam.domain.usecase.user.GetUserUseCase;
import kh.edu.istad.iam.domain.usecase.user.LoadUserForAuthenticationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class JwtCustomizerConfig {

    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    private final GetUserUseCase getUserUseCase;
    private final LoadUserForAuthenticationUseCase loadUserForAuthenticationUseCase;

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return context -> {
            String principalName = context.getPrincipal().getName();

            AuthUserResult principalUser;
            try {
                principalUser = loadUserForAuthenticationUseCase.execute(UUID.fromString(principalName));
            } catch (IllegalArgumentException e) {
                principalUser = loadUserForAuthenticationUseCase.execute(principalName);
            }
            if (principalUser == null) {
                return;
            }
            UUID userUuid = principalUser.uuid();

            context.getClaims().subject(userUuid.toString());

            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                UserAuthoritiesResult authResult = getUserAuthoritiesUseCase.execute(userUuid);
                if (authResult != null) {
                    context.getClaims().claim("roles", authResult.roles());
                    context.getClaims().claim("permissions", authResult.permissions());
                }
            }

            if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
                Set<String> authorizedScopes = context.getAuthorizedScopes();
                UserResult user = getUserUseCase.execute(userUuid);
                if (user != null) {
                    if (authorizedScopes.contains(OidcScopes.PROFILE)) {
                        String name = String.join(" ", java.util.stream.Stream.of(
                                        user.givenName(), user.middleName(), user.familyName())
                                .filter(value -> value != null && !value.isBlank())
                                .toList());
                        context.getClaims().claim("name", name);
                        addClaim(context, "given_name", user.givenName());
                        addClaim(context, "family_name", user.familyName());
                        addClaim(context, "middle_name", user.middleName());
                        addClaim(context, "nickname", user.nickname());
                        addClaim(context, "preferred_username", user.username());
                        addClaim(context, "profile", user.profileUrl());
                        addClaim(context, "picture", user.pictureUrl());
                        addClaim(context, "website", user.websiteUrl());
                        addClaim(context, "gender", user.gender());
                        addClaim(context, "birthdate", user.birthdate() == null ? null : user.birthdate().toString());
                        addClaim(context, "zoneinfo", user.zoneinfo());
                        addClaim(context, "locale", user.locale());
                        if (user.updatedAt() != null) {
                            context.getClaims().claim("updated_at", user.updatedAt().toEpochSecond());
                        }
                    }
                    if (authorizedScopes.contains(OidcScopes.EMAIL)) {
                        addClaim(context, "email", user.email());
                        context.getClaims().claim("email_verified", user.emailVerified());
                    }
                    if (authorizedScopes.contains(OidcScopes.PHONE)) {
                        addClaim(context, "phone_number", user.phoneNumber());
                        context.getClaims().claim("phone_number_verified", user.phoneNumberVerified());
                    }
                    if (authorizedScopes.contains(OidcScopes.ADDRESS)) {
                        Map<String, Object> address = new LinkedHashMap<>();
                        putIfPresent(address, "street_address", user.addressStreetAddress());
                        putIfPresent(address, "locality", user.addressLocality());
                        putIfPresent(address, "region", user.addressRegion());
                        putIfPresent(address, "postal_code", user.addressPostalCode());
                        putIfPresent(address, "country", user.addressCountry());
                        if (!address.isEmpty()) {
                            context.getClaims().claim("address", address);
                        }
                    }
                }
            }
        };
    }

    private static void addClaim(JwtEncodingContext context, String name, Object value) {
        if (value != null) {
            context.getClaims().claim(name, value);
        }
    }

    private static void putIfPresent(Map<String, Object> claims, String name, String value) {
        if (value != null) {
            claims.put(name, value);
        }
    }
}
