package kh.edu.istad.platform.gateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

import java.net.URI;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SecurityWebFilterChain gatewayFilterChain (ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchange-> exchange
                        .pathMatchers("/actuator/health/**").permitAll()
                        .pathMatchers("/api/customers/**").authenticated()
                        .anyExchange().authenticated()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .oauth2Login(oauth2 -> oauth2.authenticationFailureHandler((exchange, exception) -> {
                    log.warn("Gateway OAuth2 login failed on {} ({}): {}",
                            exchange.getExchange().getRequest().getURI().getPath(),
                            exception.getClass().getSimpleName(),
                            exception.getMessage(),
                            exception);
                    exchange.getExchange().getResponse().setStatusCode(HttpStatus.SEE_OTHER);
                    exchange.getExchange().getResponse().getHeaders().setLocation(URI.create("/login?error"));
                    return exchange.getExchange().getResponse().setComplete();
                }))
                .oauth2ResourceServer(oauth2-> oauth2
                        .jwt(Customizer.withDefaults())
                )
                .logout(logoutSpec -> logoutSpec
                        .logoutSuccessHandler(oidcLogoutSuccessHandler()))
                .build();
    }

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedServerLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedServerLogoutSuccessHandler(this.clientRegistrationRepository);

        // Sets the location that the End-User's User Agent will be redirected to
        // after the logout has been performed at the Provider
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");

        return oidcLogoutSuccessHandler;
    }
}
