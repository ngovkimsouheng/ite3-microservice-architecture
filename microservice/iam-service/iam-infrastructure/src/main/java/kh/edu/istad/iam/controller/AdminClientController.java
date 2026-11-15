package kh.edu.istad.iam.controller;

import kh.edu.istad.iam.dto.OAuth2ClientResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/admin/clients")
@RequiredArgsConstructor
@Slf4j
public class AdminClientController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    @PreAuthorize("hasAuthority('role:read')")
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
            String tokenSettings = rs.getString("token_settings");

            List<String> authMethods = parseCsv(authMethodsStr);
            List<String> grantTypes = parseCsv(grantTypesStr);
            List<String> redirectUris = parseCsv(redirectUrisStr);
            List<String> postLogoutRedirectUris = parseCsv(postLogoutStr);
            List<String> scopes = parseCsv(scopesStr);

            boolean requirePkce = clientSettings != null && clientSettings.contains("require-proof-key");
            boolean requireConsent = clientSettings != null && clientSettings.contains("require-authorization-consent");

            return new OAuth2ClientResponse(
                    id,
                    clientId,
                    clientName != null ? clientName : clientId,
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

    private List<String> parseCsv(String input) {
        if (input == null || input.isBlank()) return Collections.emptyList();
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
