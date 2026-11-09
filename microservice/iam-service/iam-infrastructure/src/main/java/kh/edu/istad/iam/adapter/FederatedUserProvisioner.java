package kh.edu.istad.iam.adapter;

import kh.edu.istad.iam.domain.dto.AuthUserResult;
import kh.edu.istad.iam.domain.dto.FederatedIdentityCommand;
import kh.edu.istad.iam.domain.usecase.identity.FindOrProvisionFederatedUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FederatedUserProvisioner {

    private static final String GITHUB_EMAILS_URI = "https://api.github.com/user/emails";
    private static final ParameterizedTypeReference<List<Map<String, Object>>> GITHUB_EMAILS_TYPE =
            new ParameterizedTypeReference<>() {};

    private final FindOrProvisionFederatedUserUseCase federatedUserUseCase;
    private final RestClient restClient = RestClient.create();

    public AuthUserResult provision(OAuth2UserRequest userRequest, Map<String, Object> attributes,
                                    String fallbackSubject) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String provider = registrationId.toUpperCase(Locale.ROOT);

        String providerSubject;
        String email = null;
        boolean emailVerified = false;
        String displayName = null;
        String username = null;
        String pictureUrl = null;

        if ("github".equalsIgnoreCase(registrationId)) {
            providerSubject = value(attributes.get("id"), fallbackSubject);
            displayName = (String) attributes.get("name");
            username = (String) attributes.get("login");
            pictureUrl = (String) attributes.get("avatar_url");

            Map<String, Object> verifiedEmail = fetchVerifiedGitHubEmail(userRequest);
            if (verifiedEmail != null && verifiedEmail.get("email") != null) {
                email = (String) verifiedEmail.get("email");
                emailVerified = true;
            } else {
                throw new OAuth2AuthenticationException(
                        new OAuth2Error("github_email_missing"),
                        "GitHub did not provide a verified email address. Add a verified email to your GitHub account and grant user:email access."
                );
            }
        } else if ("google".equalsIgnoreCase(registrationId)) {
            providerSubject = value(attributes.get("sub"), fallbackSubject);
            email = (String) attributes.get("email");
            emailVerified = Boolean.TRUE.equals(attributes.get("email_verified"));
            displayName = (String) attributes.get("name");
            pictureUrl = (String) attributes.get("picture");
        } else if ("facebook".equalsIgnoreCase(registrationId)) {
            providerSubject = value(attributes.get("id"), fallbackSubject);
            email = (String) attributes.get("email");

            emailVerified = email != null && !email.isBlank();
            displayName = (String) attributes.get("name");
        } else {
            providerSubject = fallbackSubject;
        }

        try {
            AuthUserResult authUser = federatedUserUseCase.execute(new FederatedIdentityCommand(
                    provider,
                    providerSubject,
                    email,
                    emailVerified,
                    displayName,
                    username,
                    pictureUrl,
                    attributes
            ));
            log.info("Federated login provisioned for provider {} as user {}",
                    provider, authUser.uuid());
            return authUser;
        } catch (IllegalArgumentException exception) {
            if ("facebook".equalsIgnoreCase(registrationId) && (email == null || email.isBlank())) {
                throw new OAuth2AuthenticationException(
                        new OAuth2Error("facebook_email_missing"),
                        "Facebook did not provide an email address. Check the email permission and your Facebook account.",
                        exception
                );
            }
            if ("github".equalsIgnoreCase(registrationId) && (email == null || email.isBlank())) {
                throw new OAuth2AuthenticationException(
                        new OAuth2Error("github_email_missing"),
                        "GitHub did not provide a verified email address. Add a verified email to your GitHub account and grant user:email access.",
                        exception
                );
            }
            log.warn("Federated login rejected for provider {}: {}", registrationId, exception.getMessage());
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("federated_account_rejected"),
                    "The federated account could not be linked or provisioned.",
                    exception
            );
        } catch (IllegalStateException exception) {
            log.warn("Federated login rejected for provider {}: {}", registrationId, exception.getMessage());
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("federated_account_rejected"),
                    "The federated account could not be linked or provisioned.",
                    exception
            );
        } catch (DataAccessException exception) {
            log.error("Database operation failed while provisioning federated login for provider {} ({})",
                    registrationId, exception.getClass().getSimpleName());
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("federated_persistence_failed"),
                    "The federated account could not be saved.",
                    exception
            );
        }
    }

    private Map<String, Object> fetchVerifiedGitHubEmail(OAuth2UserRequest userRequest) {
        try {
            List<Map<String, Object>> emailList = restClient
                    .get()
                    .uri(GITHUB_EMAILS_URI)
                    .header(HttpHeaders.AUTHORIZATION,
                            "Bearer " + userRequest.getAccessToken().getTokenValue())
                    .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .header("X-GitHub-Api-Version", "2022-11-28")
                    .retrieve()
                    .body(GITHUB_EMAILS_TYPE);

            if (emailList == null || emailList.isEmpty()) {
                return null;
            }

            return emailList.stream()
                    .filter(entry -> Boolean.TRUE.equals(entry.get("verified")))
                    .sorted((first, second) ->
                            Boolean.compare(Boolean.TRUE.equals(second.get("primary")),
                                    Boolean.TRUE.equals(first.get("primary"))))
                    .findFirst()
                    .orElse(null);
        } catch (RestClientException exception) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("github_email_lookup_failed"),
                    "Unable to retrieve a verified email address from GitHub.",
                    exception
            );
        }
    }

    private String value(Object value, String fallback) {
        return value == null ? fallback : value.toString();
    }
}