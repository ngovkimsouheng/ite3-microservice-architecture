package kh.edu.istad.iam.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record OAuth2ClientRequest(
        @NotBlank(message = "Client ID is required")
        String clientId,
        String clientName,
        String clientType, // "PUBLIC" or "CONFIDENTIAL"
        String clientSecret,
        List<String> grantTypes,
        List<String> redirectUris,
        List<String> postLogoutRedirectUris,
        List<String> scopes,
        Boolean requirePkce,
        Boolean requireConsent,
        Integer accessTokenTtlHours,
        Integer refreshTokenTtlDays
) {}
