package kh.edu.istad.iam.adapter;

import kh.edu.istad.iam.domain.dto.ConnectedAppResult;
import kh.edu.istad.iam.domain.port.out.ConnectedAppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcConnectedAppRepositoryAdapter implements ConnectedAppRepository {

    private final JdbcTemplate jdbcTemplate;
    private final OAuth2AuthorizationConsentService consentService;

    @Override
    public List<ConnectedAppResult> findAuthorizedForPrincipal(String principalName) {
        return jdbcTemplate.query("""
                SELECT client.client_id, client.client_name, consent.authorities
                FROM oauth2_authorization_consent consent
                JOIN oauth2_registered_client client
                  ON client.id = consent.registered_client_id
                WHERE consent.principal_name = ?
                ORDER BY client.client_name
                """, (resultSet, rowNumber) -> new ConnectedAppResult(
                resultSet.getString("client_id"),
                resultSet.getString("client_name"),
                Arrays.stream(resultSet.getString("authorities").split(","))
                        .map(String::trim)
                        .filter(value -> !value.isEmpty())
                        .map(value -> value.startsWith("SCOPE_") ? value.substring("SCOPE_".length()) : value)
                        .collect(Collectors.toUnmodifiableSet())
        ), principalName);
    }

    @Override
    public void revokeAuthorization(String principalName, String clientId) {
        OAuth2AuthorizationConsent consent = consentService.findById(principalName, clientId);
        if (consent == null) {
            throw new IllegalArgumentException("Authorized application not found.");
        }
        consentService.remove(consent);
    }
}
