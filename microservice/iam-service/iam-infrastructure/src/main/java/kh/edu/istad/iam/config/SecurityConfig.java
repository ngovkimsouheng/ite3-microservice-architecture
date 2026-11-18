package kh.edu.istad.iam.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import kh.edu.istad.iam.adapter.CustomOAuth2UserService;
import kh.edu.istad.iam.adapter.CustomOidcUserService;
import kh.edu.istad.iam.domain.usecase.security.RecordSecurityEventUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyFactory;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JdbcTemplate jdbcTemplate;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;
    private final IamJwtAuthenticationConverter iamJwtAuthenticationConverter;
    private final ApiProblemDetailHandler apiProblemDetailHandler;
    private final RecordSecurityEventUseCase recordSecurityEventUseCase;

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();

        http
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, authorizationServer ->
                        authorizationServer
                                .authorizationEndpoint(endpoint ->
                                        endpoint.consentPage("/oauth2/consent"))
                                .oidc(oidc -> oidc
                                        .clientRegistrationEndpoint(Customizer.withDefaults())
                                )
                )
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(HttpMethod.POST, "/connect/register")
                                .hasAuthority("SCOPE_client.create")
                                .requestMatchers(HttpMethod.GET, "/connect/register")
                                .hasAuthority("SCOPE_client.read")
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .exceptionHandling(exceptions -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                );

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                // Allow both browser session cookies and Bearer tokens:
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**", "/api/admin/system/config/password-policy").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(iamJwtAuthenticationConverter))
                        .authenticationEntryPoint(apiProblemDetailHandler)
                        .accessDeniedHandler(apiProblemDetailHandler)
                );

        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // <-- ADD THIS LINE to allow form login POST!
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/index.html",
                                "/favicon.svg",
                                "/icons.svg",
                                "/logo.png",
                                "/assets/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/oauth2/**",
                                "/login/oauth2/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            jakarta.servlet.http.HttpSession session = request.getSession(true);
                            if (session != null) {
                                session.setAttribute("IP_ADDRESS", extractClientIp(request));
                                session.setAttribute("USER_AGENT", parseDevice(request.getHeader("User-Agent")));
                            }
                            recordLoginEvent(request, authentication, true, null);
                            response.sendRedirect("/account/profile");
                        })
                        .failureHandler((request, response, exception) -> {
                            recordLoginEvent(request, null, false, exception.getMessage());
                            response.sendRedirect("/login?error");
                        })
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                                .oidcUserService(customOidcUserService)
                        )
                        .successHandler((request, response, authentication) -> {
                            jakarta.servlet.http.HttpSession session = request.getSession(false);
                            if (session != null) {
                                session.setAttribute("IP_ADDRESS", extractClientIp(request));
                                session.setAttribute("USER_AGENT", parseDevice(request.getHeader("User-Agent")));
                                if (session.getAttribute("iam.link-success") != null) {
                                    String linkedProv = (String) session.getAttribute("iam.link-success");
                                    session.removeAttribute("iam.link-success");
                                    response.sendRedirect("/account/linked-accounts?linked=" + linkedProv.toLowerCase(java.util.Locale.ROOT));
                                    return;
                                }
                            }
                            recordOAuth2LoginEvent(request, authentication);
                            response.sendRedirect("/account/profile");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            if (authentication != null) {
                                recordLogoutEvent(request, authentication);
                            }
                            response.sendRedirect("/login?logout");
                        })
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService() {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository());
    }

    @Bean
    public OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService() {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository());
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(
            @Value("${IAM_JWK_PRIVATE_KEY:}") String configuredPrivateKey,
            Environment environment
    ) {
        if (configuredPrivateKey.isBlank() && !environment.acceptsProfiles(Profiles.of("dev"))) {
            throw new IllegalStateException("IAM_JWK_PRIVATE_KEY must be configured outside the dev profile.");
        }
        KeyPair keyPair = configuredPrivateKey.isBlank()
                ? generateRsaKey()
                : loadRsaKey(configuredPrivateKey);
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.nameUUIDFromBytes(publicKey.getEncoded()).toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private static KeyPair loadRsaKey(String pem) {
        try {
            String encodedKey = pem.replace("\\n", "\n")
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            RSAPrivateCrtKey privateKey = (RSAPrivateCrtKey) KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(encodedKey)));
            RSAPublicKey publicKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent()));
            return new KeyPair(publicKey, privateKey);
        } catch (GeneralSecurityException | IllegalArgumentException exception) {
            throw new IllegalStateException(
                    "IAM_JWK_PRIVATE_KEY must contain a valid RSA PKCS#8 private key.", exception);
        }
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings(
            @Value("${spring.security.oauth2.authorizationserver.issuer:http://localhost:9000}") String issuer
    ) {
        return AuthorizationServerSettings.builder()
                .issuer(issuer)
                .build();
    }

    private void recordLoginEvent(HttpServletRequest request, org.springframework.security.core.Authentication authentication, boolean success, String error) {
        try {
            String ip = extractClientIp(request);
            String userAgent = request.getHeader("User-Agent");
            String device = parseDevice(userAgent);
            String location = "Phnom Penh, Cambodia";

            if (success && authentication != null) {
                UUID userUuid = null;
                if (authentication.getPrincipal() instanceof IamUserDetails iamUser) {
                    userUuid = iamUser.getUuid();
                }
                recordSecurityEventUseCase.execute(
                        userUuid,
                        authentication.getName(),
                        "LOGIN",
                        "LOGIN_SUCCESS",
                        "SUCCESS",
                        ip,
                        device,
                        location,
                        "Password",
                        "Successful sign-in via Password",
                        null
                );
            } else {
                String attemptedUsername = request.getParameter("username");
                if (attemptedUsername == null || attemptedUsername.isBlank()) {
                    attemptedUsername = "Unknown";
                }
                recordSecurityEventUseCase.execute(
                        null,
                        attemptedUsername,
                        "LOGIN",
                        "LOGIN_FAILED",
                        "FAILED",
                        ip,
                        device,
                        location,
                        "Password",
                        "Failed sign-in attempt: " + (error != null ? error : "Bad credentials"),
                        null
                );
            }
        } catch (Exception ex) {
            log.error("Failed to record login event: {}", ex.getMessage());
        }
    }

    private void recordOAuth2LoginEvent(HttpServletRequest request, org.springframework.security.core.Authentication authentication) {
        try {
            if (authentication == null) return;
            String ip = extractClientIp(request);
            String userAgent = request.getHeader("User-Agent");
            String device = parseDevice(userAgent);
            String location = "Phnom Penh, Cambodia";

            String provider = "Social SSO";
            if (authentication instanceof org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken oauthToken) {
                provider = formatProvider(oauthToken.getAuthorizedClientRegistrationId());
            }

            UUID userUuid = null;
            String username = null;

            if (authentication.getPrincipal() instanceof IamUserDetails iamUser) {
                userUuid = iamUser.getUuid();
                username = iamUser.getUsername();
            } else if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser oidcUser) {
                Object iamUserId = oidcUser.getAttribute("iam_user_id");
                if (iamUserId != null) {
                    try {
                        userUuid = UUID.fromString(iamUserId.toString());
                    } catch (Exception ignored) {}
                }
                username = (String) oidcUser.getAttribute("iam_username");
                if (username == null) username = oidcUser.getPreferredUsername();
                if (username == null) username = (String) oidcUser.getAttribute("name");
            } else if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User oAuth2User) {
                Object iamUserId = oAuth2User.getAttribute("iam_user_id");
                if (iamUserId == null) iamUserId = oAuth2User.getAttribute("sub");
                if (iamUserId != null) {
                    try {
                        userUuid = UUID.fromString(iamUserId.toString());
                    } catch (Exception ignored) {}
                }
                username = (String) oAuth2User.getAttribute("iam_username");
                if (username == null) username = (String) oAuth2User.getAttribute("name");
            }

            if (userUuid == null && authentication.getName() != null) {
                try {
                    userUuid = UUID.fromString(authentication.getName());
                } catch (Exception ignored) {}
            }

            if (username == null || username.isBlank() || (userUuid != null && username.equals(userUuid.toString()))) {
                username = authentication.getName();
            }

            recordSecurityEventUseCase.execute(
                    userUuid,
                    username,
                    "LOGIN",
                    "LOGIN_SUCCESS",
                    "SUCCESS",
                    ip,
                    device,
                    location,
                    provider,
                    "Successful sign-in via " + provider,
                    null
            );
        } catch (Exception ex) {
            log.error("Failed to record OAuth2 login event: {}", ex.getMessage());
        }
    }

    private void recordLogoutEvent(HttpServletRequest request, org.springframework.security.core.Authentication authentication) {
        try {
            if (authentication == null) return;
            String ip = extractClientIp(request);
            UUID userUuid = null;
            if (authentication.getPrincipal() instanceof IamUserDetails iamUser) {
                userUuid = iamUser.getUuid();
            }

            recordSecurityEventUseCase.execute(
                    userUuid,
                    authentication.getName(),
                    "ACTIVITY",
                    "SESSION_TERMINATED",
                    "SUCCESS",
                    ip,
                    null,
                    "Phnom Penh, Cambodia",
                    null,
                    "Account Signed Out",
                    "User signed out of web session"
            );
        } catch (Exception ex) {
            log.error("Failed to record logout event: {}", ex.getMessage());
        }
    }

    private String extractClientIp(HttpServletRequest request) {
        if (request == null) return "127.0.0.1 (Localhost)";
        String ip = null;
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank() && !"unknown".equalsIgnoreCase(xf)) {
            ip = xf.split(",")[0].trim();
        }
        if (ip == null || ip.isBlank()) {
            String xr = request.getHeader("X-Real-IP");
            if (xr != null && !xr.isBlank() && !"unknown".equalsIgnoreCase(xr)) {
                ip = xr.trim();
            }
        }
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        if (ip == null || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("::1") || ip.equals("127.0.0.1")) {
            return "127.0.0.1 (Localhost)";
        }
        return ip;
    }

    private String parseDevice(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) return "Web Browser / Desktop";

        String browser = "Browser";
        if (userAgent.contains("Edg/")) browser = "Edge";
        else if (userAgent.contains("Chrome/")) browser = "Chrome";
        else if (userAgent.contains("Safari/") && !userAgent.contains("Chrome/")) browser = "Safari";
        else if (userAgent.contains("Firefox/")) browser = "Firefox";

        String os = "Desktop";
        if (userAgent.contains("Windows NT 10.0")) os = "Windows 11/10";
        else if (userAgent.contains("Windows")) os = "Windows";
        else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) os = "iOS";
        else if (userAgent.contains("Android")) os = "Android";
        else if (userAgent.contains("Macintosh") || userAgent.contains("Mac OS X")) os = "macOS";
        else if (userAgent.contains("Linux")) os = "Linux";

        return browser + " / " + os;
    }

    private String formatProvider(String providerId) {
        if (providerId == null) return "Social SSO";
        return switch (providerId.toLowerCase()) {
            case "google" -> "Google SSO";
            case "github" -> "GitHub SSO";
            case "facebook" -> "Facebook SSO";
            case "telegram" -> "Telegram";
            default -> providerId.substring(0, 1).toUpperCase() + providerId.substring(1) + " SSO";
        };
    }
}