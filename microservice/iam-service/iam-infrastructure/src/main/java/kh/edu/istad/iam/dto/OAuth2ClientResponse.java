package kh.edu.istad.iam.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record OAuth2ClientResponse(
        String id,
        String clientId,
        String clientName,
        String clientType, // "PUBLIC" or "CONFIDENTIAL"
        OffsetDateTime clientIssuedAt,
        List<String> authMethods,
        List<String> grantTypes,
        List<String> redirectUris,
        List<String> postLogoutRedirectUris,
        List<String> scopes,
        boolean requirePkce,
        boolean requireConsent,
        String accessTokenTtl,
        String refreshTokenTtl,
        String status
) {}
