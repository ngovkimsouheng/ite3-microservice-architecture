package kh.edu.istad.iam.domain.dto;

import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.UUID;

public record UserResult(
            UUID uuid,
            String username,
            String email,
            boolean emailVerified,
            String givenName,
            String familyName,
            String middleName,
            String nickname,
            String gender,
            LocalDate birthdate,
            String profileUrl,
            String pictureUrl,
            String websiteUrl,
            String zoneinfo,
            String locale,
            String phoneNumber,
            boolean phoneNumberVerified,
            String addressStreetAddress,
            String addressLocality,
            String addressRegion,
            String addressPostalCode,
            String addressCountry,
            boolean enabled,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {}
