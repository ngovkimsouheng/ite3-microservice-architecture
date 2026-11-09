package kh.edu.istad.iam.domain.dto;

import java.util.Map;

public record FederatedIdentityCommand(
            String provider,
            String providerSubject,
            String email,
            boolean emailVerified,
            String displayName,
            String username,
            String pictureUrl,
            Map<String, Object> attributes
    ) {}
