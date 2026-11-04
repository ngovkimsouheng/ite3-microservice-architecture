package kh.edu.istad.iam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

@Configuration
public class TokenConfig {

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return (context) -> {

            SecurityContext securityContext = SecurityContextHolder.getContext();
            Authentication authentication = securityContext.getAuthentication();
            var printcipal = authentication.getPrincipal();

            if (printcipal instanceof OAuth2User) {
                if (OAuth2ParameterNames.ACCESS_TOKEN.equals(context.getTokenType().getValue())) {
                    context.getClaims().claim(
                            claim -> claim.put("username", ((OAuth2User) printcipal).getName()));
                 );
                }
            }

            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                context.getClaims().claim("username", username);
            }

//            if (OAuth2ParameterNames.ACCESS_TOKEN.equals(context.getTokenType().getValue())) {
//
//                context.getClaims().claim();
//            }
        };
    }
}