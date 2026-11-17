package kh.edu.istad.iam.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kh.edu.istad.iam.domain.usecase.security.RecordSecurityEventUseCase;
import kh.edu.istad.iam.dto.OAuth2ClientRequest;
import kh.edu.istad.iam.dto.OAuth2ClientResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@RestController
@RequestMapping("/api/admin/clients")
@RequiredArgsConstructor
@Slf4j
public class AdminClientController {

    private final JdbcTemplate jdbcTemplate;
    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;
    private final RecordSecurityEventUseCase recordSecurityEventUseCase;

    @GetMapping
    @PreAuthorize("hasAuthority('role:read') or hasRole('ADMIN')")
    public ResponseEntity<List<OAuth2ClientResponse>> listRegisteredClients() {
        String sql = """
            SELECT id, client_id, client_id_issued_at, client_name,
                   client_authentication_methods, authorization_grant_types,
                   redirect_uris, post_logout_redirect_uris, scopes,
                   client_settings, token_settings
            FROM oauth2_registered_client
            ORDER BY client_id_issued_at DESC
        """;

        List<OAuth2ClientResponse> clients = jdbcTemplate.query(sql, (rs, rowNum) -> {
            String id = rs.getString("id");
            String clientId = rs.getString("client_id");
            String clientName = rs.getString("client_name");
            Timestamp issuedAtTs = rs.getTimestamp("client_id_issued_at");
            OffsetDateTime issuedAt = issuedAtTs != null ? issuedAtTs.toInstant().atOffset(ZoneOffset.UTC) : OffsetDateTime.now();

            String authMethodsStr = rs.getString("client_authentication_methods");
            String grantTypesStr = rs.getString("authorization_grant_types");
            String redirectUrisStr = rs.getString("redirect_uris");
            String postLogoutStr = rs.getString("post_logout_redirect_uris");
            String scopesStr = rs.getString("scopes");
            String clientSettings = rs.getString("client_settings");

            List<String> authMethods = parseCsv(authMethodsStr);
            List<String> grantTypes = parseCsv(grantTypesStr);
            List<String> redirectUris = parseCsv(redirectUrisStr);
            List<String> postLogoutRedirectUris = parseCsv(postLogoutStr);
            List<String> scopes = parseCsv(scopesStr);

            boolean requirePkce = clientSettings != null && clientSettings.contains("require-proof-key");
            boolean requireConsent = clientSettings != null && clientSettings.contains("require-authorization-consent");

            boolean isPublic = authMethods.contains("none") || (!authMethods.contains("client_secret_basic") && !authMethods.contains("client_secret_post"));
            String clientType = isPublic ? "PUBLIC" : "CONFIDENTIAL";

            return new OAuth2ClientResponse(
                    id,
                    clientId,
                    clientName != null ? clientName : clientId,
                    clientType,
                    issuedAt,
                    authMethods,
                    grantTypes,
                    redirectUris,
                    postLogoutRedirectUris,
                    scopes,
                    requirePkce,
                    requireConsent,
                    "5 hours",
                    "7 days",
                    "ACTIVE"
            );
        });

        return ResponseEntity.ok(clients);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('role:update') or hasRole('ADMIN')")
    public ResponseEntity<OAuth2ClientResponse> registerClient(
            @Valid @RequestBody OAuth2ClientRequest request,
            HttpServletRequest servletRequest
    ) {
        if (registeredClientRepository.findByClientId(request.clientId().trim()) != null) {
            return ResponseEntity.badRequest().build();
        }

        boolean isPublic = "PUBLIC".equalsIgnoreCase(request.clientType()) ||
                (request.clientSecret() == null || request.clientSecret().isBlank());

        String newId = UUID.randomUUID().toString();
        RegisteredClient.Builder builder = RegisteredClient.withId(newId)
                .clientId(request.clientId().trim())
                .clientName(request.clientName() != null && !request.clientName().isBlank()
                        ? request.clientName().trim() : request.clientId().trim());

        if (isPublic) {
            builder.clientAuthenticationMethod(ClientAuthenticationMethod.NONE);
        } else {
            builder.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
            builder.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST);
            String secret = request.clientSecret() != null && !request.clientSecret().isBlank()
                    ? request.clientSecret().trim() : "secret";
            builder.clientSecret(passwordEncoder.encode(secret));
        }

        // Grant Types
        List<String> grants = request.grantTypes() != null && !request.grantTypes().isEmpty()
                ? request.grantTypes()
                : (isPublic ? List.of("authorization_code", "refresh_token") : List.of("authorization_code", "refresh_token", "client_credentials"));

        for (String grant : grants) {
            if ("authorization_code".equalsIgnoreCase(grant)) {
                builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
            } else if ("refresh_token".equalsIgnoreCase(grant)) {
                builder.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN);
            } else if ("client_credentials".equalsIgnoreCase(grant)) {
                builder.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS);
            }
        }

        // Redirect URIs
        if (request.redirectUris() != null) {
            for (String uri : request.redirectUris()) {
                if (uri != null && !uri.isBlank()) {
                    builder.redirectUri(uri.trim());
                }
            }
        }

        // Post logout redirect URIs
        if (request.postLogoutRedirectUris() != null) {
            for (String uri : request.postLogoutRedirectUris()) {
                if (uri != null && !uri.isBlank()) {
                    builder.postLogoutRedirectUri(uri.trim());
                }
            }
        }

        // Scopes
        List<String> scopes = request.scopes() != null && !request.scopes().isEmpty()
                ? request.scopes()
                : List.of("openid", "profile");
        for (String scope : scopes) {
            if (scope != null && !scope.isBlank()) {
                builder.scope(scope.trim());
            }
        }

        // Client Settings
        boolean requirePkce = request.requirePkce() != null ? request.requirePkce() : isPublic;
        boolean requireConsent = request.requireConsent() != null ? request.requireConsent() : true;
        builder.clientSettings(ClientSettings.builder()
                .requireProofKey(requirePkce)
                .requireAuthorizationConsent(requireConsent)
                .build());

        // Token Settings
        int accessHours = request.accessTokenTtlHours() != null ? request.accessTokenTtlHours() : 5;
        int refreshDays = request.refreshTokenTtlDays() != null ? request.refreshTokenTtlDays() : 7;
        builder.tokenSettings(TokenSettings.builder()
                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                .accessTokenTimeToLive(Duration.ofHours(accessHours))
                .refreshTokenTimeToLive(Duration.ofDays(refreshDays))
                .reuseRefreshTokens(false)
                .build());

        RegisteredClient saved = builder.build();
        registeredClientRepository.save(saved);

        logAudit("CLIENT_REGISTERED", "Registered new OAuth2 client: " + saved.getClientId() + " (" + (isPublic ? "PUBLIC" : "CONFIDENTIAL") + ")", servletRequest);

        return ResponseEntity.ok(toResponse(saved, isPublic));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:update') or hasRole('ADMIN')")
    public ResponseEntity<OAuth2ClientResponse> updateClient(
            @PathVariable("id") String id,
            @Valid @RequestBody OAuth2ClientRequest request,
            HttpServletRequest servletRequest
    ) {
        RegisteredClient existing = registeredClientRepository.findById(id);
        if (existing == null) {
            existing = registeredClientRepository.findByClientId(id);
        }
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        boolean isPublic = "PUBLIC".equalsIgnoreCase(request.clientType());
        RegisteredClient.Builder builder = RegisteredClient.from(existing);

        if (request.clientName() != null && !request.clientName().isBlank()) {
            builder.clientName(request.clientName().trim());
        }

        if (isPublic) {
            builder.clientAuthenticationMethods(methods -> {
                methods.clear();
                methods.add(ClientAuthenticationMethod.NONE);
            });
            builder.clientSecret(null);
        } else {
            builder.clientAuthenticationMethods(methods -> {
                methods.clear();
                methods.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
                methods.add(ClientAuthenticationMethod.CLIENT_SECRET_POST);
            });
            if (request.clientSecret() != null && !request.clientSecret().isBlank()) {
                builder.clientSecret(passwordEncoder.encode(request.clientSecret().trim()));
            }
        }

        if (request.grantTypes() != null && !request.grantTypes().isEmpty()) {
            builder.authorizationGrantTypes(grants -> {
                grants.clear();
                for (String g : request.grantTypes()) {
                    if ("authorization_code".equalsIgnoreCase(g)) grants.add(AuthorizationGrantType.AUTHORIZATION_CODE);
                    else if ("refresh_token".equalsIgnoreCase(g)) grants.add(AuthorizationGrantType.REFRESH_TOKEN);
                    else if ("client_credentials".equalsIgnoreCase(g)) grants.add(AuthorizationGrantType.CLIENT_CREDENTIALS);
                }
            });
        }

        if (request.redirectUris() != null) {
            builder.redirectUris(uris -> {
                uris.clear();
                for (String u : request.redirectUris()) {
                    if (u != null && !u.isBlank()) uris.add(u.trim());
                }
            });
        }

        if (request.scopes() != null) {
            builder.scopes(s -> {
                s.clear();
                for (String sc : request.scopes()) {
                    if (sc != null && !sc.isBlank()) s.add(sc.trim());
                }
            });
        }

        boolean requirePkce = request.requirePkce() != null ? request.requirePkce() : isPublic;
        boolean requireConsent = request.requireConsent() != null ? request.requireConsent() : true;
        builder.clientSettings(ClientSettings.builder()
                .requireProofKey(requirePkce)
                .requireAuthorizationConsent(requireConsent)
                .build());

        RegisteredClient updated = builder.build();
        registeredClientRepository.save(updated);

        logAudit("CLIENT_UPDATED", "Updated OAuth2 client settings for: " + updated.getClientId(), servletRequest);

        return ResponseEntity.ok(toResponse(updated, isPublic));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:delete') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClient(
            @PathVariable("id") String id,
            HttpServletRequest servletRequest
    ) {
        RegisteredClient existing = registeredClientRepository.findById(id);
        if (existing == null) {
            existing = registeredClientRepository.findByClientId(id);
        }
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        // Clean up authorizations & consents referencing this client
        jdbcTemplate.update("DELETE FROM oauth2_authorization_consent WHERE registered_client_id = ?", existing.getId());
        jdbcTemplate.update("DELETE FROM oauth2_authorization WHERE registered_client_id = ?", existing.getId());
        jdbcTemplate.update("DELETE FROM oauth2_registered_client WHERE id = ?", existing.getId());

        logAudit("CLIENT_DELETED", "Deleted OAuth2 registered client: " + existing.getClientId(), servletRequest);

        return ResponseEntity.noContent().build();
    }

    private OAuth2ClientResponse toResponse(RegisteredClient c, boolean isPublic) {
        List<String> authMethods = c.getClientAuthenticationMethods().stream()
                .map(ClientAuthenticationMethod::getValue).toList();
        List<String> grantTypes = c.getAuthorizationGrantTypes().stream()
                .map(AuthorizationGrantType::getValue).toList();
        List<String> redirectUris = new ArrayList<>(c.getRedirectUris());
        List<String> postLogout = new ArrayList<>(c.getPostLogoutRedirectUris());
        List<String> scopes = new ArrayList<>(c.getScopes());

        boolean pkce = c.getClientSettings() != null && c.getClientSettings().isRequireProofKey();
        boolean consent = c.getClientSettings() != null && c.getClientSettings().isRequireAuthorizationConsent();

        return new OAuth2ClientResponse(
                c.getId(),
                c.getClientId(),
                c.getClientName() != null ? c.getClientName() : c.getClientId(),
                isPublic ? "PUBLIC" : "CONFIDENTIAL",
                c.getClientIdIssuedAt() != null ? c.getClientIdIssuedAt().atOffset(ZoneOffset.UTC) : OffsetDateTime.now(),
                authMethods,
                grantTypes,
                redirectUris,
                postLogout,
                scopes,
                pkce,
                consent,
                "5 hours",
                "7 days",
                "ACTIVE"
        );
    }

    private void logAudit(String eventType, String desc, HttpServletRequest request) {
        try {
            String actor = SecurityContextHolder.getContext().getAuthentication() != null
                    ? SecurityContextHolder.getContext().getAuthentication().getName()
                    : "ADMIN";
            String ip = request != null ? request.getRemoteAddr() : "127.0.0.1";
            recordSecurityEventUseCase.execute(
                    null,
                    actor,
                    "ACTIVITY",
                    eventType,
                    "SUCCESS",
                    ip,
                    request != null ? request.getHeader("User-Agent") : "Web Browser",
                    "Phnom Penh, Cambodia",
                    null,
                    "OAuth2 Client Governance",
                    desc
            );
        } catch (Exception ex) {
            log.error("Failed to log client audit event: {}", ex.getMessage());
        }
    }

    private List<String> parseCsv(String input) {
        if (input == null || input.isBlank()) return Collections.emptyList();
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
