package kh.edu.istad.iam.config;

import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.time.Duration;
import java.util.*;

@Configuration
@RequiredArgsConstructor
public class TokenConfig {

    private final UserRepository userRepository;
    @Bean
    public TokenSettings tokenSettings() {
        return TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofHours(4))
                .refreshTokenTimeToLive(Duration.ofDays(30))
                .reuseRefreshTokens(true)
                .build();
    }
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return context -> {
            Authentication principal = context.getPrincipal();
            if (principal == null) {
                return;
            }

            // Extract UUID subject (sub = users.uuid)
            String sub = principal.getName();
            UUID userUuid;
            try {
                userUuid = UUID.fromString(sub);
            } catch (IllegalArgumentException e) {
                // If authenticated via client_credentials, sub is client_id (not a user UUID)
                return;
            }

            Optional<User> userOpt = userRepository.findByUuid(userUuid);
            if (userOpt.isEmpty()) {
                return;
            }
            User user = userOpt.get();

            // Section E.1: Access Token roles and permissions
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                Set<String> roles = new HashSet<>();
                Set<String> permissions = new HashSet<>();

                for (GrantedAuthority ga : principal.getAuthorities()) {
                    String auth = ga.getAuthority();
                    if (auth.startsWith("ROLE_")) {
                        roles.add(auth);
                    } else {
                        permissions.add(auth);
                    }
                }

                context.getClaims().claims(claims -> {
                    claims.put("roles", roles);
                    claims.put("permissions", permissions);
                    claims.put("preferred_username", user.getUsername());
                });
            }

            // Section E.2: ID Token claims according to scopes
            if ("id_token".equals(context.getTokenType().getValue())) {
                Set<String> authorizedScopes = context.getAuthorizedScopes();

                context.getClaims().claims(claims -> {
                    if (authorizedScopes.contains("profile")) {
                        claims.put("name", user.getGivenName() + " " + user.getFamilyName());
                        claims.put("given_name", user.getGivenName());
                        claims.put("family_name", user.getFamilyName());
                        if (user.getMiddleName() != null) claims.put("middle_name", user.getMiddleName());
                        if (user.getNickname() != null) claims.put("nickname", user.getNickname());
                        claims.put("preferred_username", user.getUsername());
                        if (user.getProfileUrl() != null) claims.put("profile", user.getProfileUrl());
                        if (user.getPictureUrl() != null) claims.put("picture", user.getPictureUrl());
                        if (user.getWebsiteUrl() != null) claims.put("website", user.getWebsiteUrl());
                        if (user.getGender() != null) claims.put("gender", user.getGender());
                        if (user.getBirthdate() != null) claims.put("birthdate", user.getBirthdate().toString());
                        if (user.getZoneinfo() != null) claims.put("zoneinfo", user.getZoneinfo());
                        claims.put("locale", user.getLocale());
                        if (user.getUpdatedAt() != null) claims.put("updated_at", user.getUpdatedAt().toEpochSecond());
                    }
                    if (authorizedScopes.contains("email")) {
                        claims.put("email", user.getEmail());
                        claims.put("email_verified", user.isEmailVerified());
                    }
                    if (authorizedScopes.contains("phone") && user.getPhoneNumber() != null) {
                        claims.put("phone_number", user.getPhoneNumber());
                        claims.put("phone_number_verified", user.isPhoneNumberVerified());
                    }
                });
            }
        };
    }
}