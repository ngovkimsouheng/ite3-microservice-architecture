package kh.edu.istad.iam.restapi.dto;

import kh.edu.istad.iam.domain.core.entity.SystemConfiguration;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record SystemConfigResponse(
        Long id,

        // 1. Authentication Policies
        Integer maxFailedAttempts,
        Integer lockoutDurationMinutes,
        Boolean enableAutoUnlock,
        String mfaPolicy,

        // 2. Password Policies
        Integer minPasswordLength,
        Boolean requireUppercase,
        Boolean requireLowercase,
        Boolean requireDigits,
        Boolean requireSpecialChar,
        Integer passwordExpireDays,

        // 3. Session Expiration Policies
        Integer sessionIdleTimeoutMinutes,
        Integer sessionMaxInactiveDays,
        Integer maxConcurrentSessions,
        Boolean forceSingleSession,

        // 4. Token Lifetimes & Settings
        Integer accessTokenTtlHours,
        Integer refreshTokenTtlDays,
        Integer authCodeTtlMinutes,
        Boolean reuseRefreshTokens,
        String tokenFormat,

        OffsetDateTime updatedAt,
        String updatedBy
) {
    public static SystemConfigResponse fromDomain(SystemConfiguration entity) {
        return SystemConfigResponse.builder()
                .id(entity.getId())
                .maxFailedAttempts(entity.getMaxFailedAttempts())
                .lockoutDurationMinutes(entity.getLockoutDurationMinutes())
                .enableAutoUnlock(entity.getEnableAutoUnlock())
                .mfaPolicy(entity.getMfaPolicy())
                .minPasswordLength(entity.getMinPasswordLength())
                .requireUppercase(entity.getRequireUppercase())
                .requireLowercase(entity.getRequireLowercase())
                .requireDigits(entity.getRequireDigits())
                .requireSpecialChar(entity.getRequireSpecialChar())
                .passwordExpireDays(entity.getPasswordExpireDays())
                .sessionIdleTimeoutMinutes(entity.getSessionIdleTimeoutMinutes())
                .sessionMaxInactiveDays(entity.getSessionMaxInactiveDays())
                .maxConcurrentSessions(entity.getMaxConcurrentSessions())
                .forceSingleSession(entity.getForceSingleSession())
                .accessTokenTtlHours(entity.getAccessTokenTtlHours())
                .refreshTokenTtlDays(entity.getRefreshTokenTtlDays())
                .authCodeTtlMinutes(entity.getAuthCodeTtlMinutes())
                .reuseRefreshTokens(entity.getReuseRefreshTokens())
                .tokenFormat(entity.getTokenFormat())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
