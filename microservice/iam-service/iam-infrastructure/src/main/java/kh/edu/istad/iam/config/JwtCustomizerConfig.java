/*
package kh.edu.istad.iam.infrastructure.config;

import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class JwtCustomizerConfig {

    private final UserRepository userRepository;

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                String principalName = context.getPrincipal().getName();

                // 1. Look up user and populate standard OIDC claims
                userRepository.findByUsername(principalName).ifPresent(user -> {
                    context.getClaims().claim("user_uuid", user.getUuid().toString());
                    context.getClaims().claim("email", user.getEmail());
                    context.getClaims().claim("name", user.getGivenName() + " " + user.getFamilyName());

                    // 2. Fetch roles & effective permissions per Section B.6
                    Set<String> authorities = userRepository.findEffectiveAuthorities(user.getId());
                    context.getClaims().claim("authorities", authorities);
                });
            }
        };
    }
}*/
