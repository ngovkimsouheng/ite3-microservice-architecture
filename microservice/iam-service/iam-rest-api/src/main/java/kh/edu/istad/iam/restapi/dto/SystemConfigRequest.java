package kh.edu.istad.iam.restapi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kh.edu.istad.iam.domain.core.entity.SystemConfiguration;

import java.time.OffsetDateTime;

public record SystemConfigRequest(
        // 1. Authentication Policies
        @NotNull(message = "maxFailedAttempts is required")
        @Min(value = 1, message = "maxFailedAttempts must be at least 1")
        @Max(value = 50, message = "maxFailedAttempts cannot exceed 50")
        Integer maxFailedAttempts,

        @NotNull(message = "lockoutDurationMinutes is required")
        @Min(value = 1, message = "lockoutDurationMinutes must be at least 1")
        @Max(value = 10080, message = "lockoutDurationMinutes cannot exceed 7 days")
        Integer lockoutDurationMinutes,

        @NotNull(message = "enableAutoUnlock is required")
        Boolean enableAutoUnlock,

        @NotBlank(message = "mfaPolicy is required")
        String mfaPolicy,

        // 2. Password Policies
        @NotNull(message = "minPasswordLength is required")
        @Min(value = 6, message = "minPasswordLength must be at least 6")
        @Max(value = 128, message = "minPasswordLength cannot exceed 128")
        Integer minPasswordLength,

        @NotNull(message = "requireUppercase is required")
        Boolean requireUppercase,

        @NotNull(message = "requireLowercase is required")
        Boolean requireLowercase,

        @NotNull(message = "requireDigits is required")
        Boolean requireDigits,

        @NotNull(message = "requireSpecialChar is required")
        Boolean requireSpecialChar,

        @NotNull(message = "passwordExpireDays is required")
        @Min(value = 0, message = "passwordExpireDays must be at least 0 (0 to disable)")
        @Max(value = 730, message = "passwordExpireDays cannot exceed 730")
        Integer passwordExpireDays,

        // 3. Session Expiration Policies
        @NotNull(message = "sessionIdleTimeoutMinutes is required")
        @Min(value = 1, message = "sessionIdleTimeoutMinutes must be at least 1")
        @Max(value = 1440, message = "sessionIdleTimeoutMinutes cannot exceed 1440")
        Integer sessionIdleTimeoutMinutes,

        @NotNull(message = "sessionMaxInactiveDays is required")
        @Min(value = 1, message = "sessionMaxInactiveDays must be at least 1")
        @Max(value = 365, message = "sessionMaxInactiveDays cannot exceed 365")
        Integer sessionMaxInactiveDays,

        @NotNull(message = "maxConcurrentSessions is required")
        @Min(value = 1, message = "maxConcurrentSessions must be at least 1")
        @Max(value = 50, message = "maxConcurrentSessions cannot exceed 50")
        Integer maxConcurrentSessions,

        @NotNull(message = "forceSingleSession is required")
        Boolean forceSingleSession,

        // 4. Token Lifetimes & Settings
        @NotNull(message = "accessTokenTtlHours is required")
        @Min(value = 1, message = "accessTokenTtlHours must be at least 1")
        @Max(value = 720, message = "accessTokenTtlHours cannot exceed 720")
        Integer accessTokenTtlHours,

        @NotNull(message = "refreshTokenTtlDays is required")
        @Min(value = 1, message = "refreshTokenTtlDays must be at least 1")
        @Max(value = 365, message = "refreshTokenTtlDays cannot exceed 365")
        Integer refreshTokenTtlDays,

        @NotNull(message = "authCodeTtlMinutes is required")
        @Min(value = 1, message = "authCodeTtlMinutes must be at least 1")
        @Max(value = 60, message = "authCodeTtlMinutes cannot exceed 60")
        Integer authCodeTtlMinutes,

        @NotNull(message = "reuseRefreshTokens is required")
        Boolean reuseRefreshTokens,

        @NotBlank(message = "tokenFormat is required")
        String tokenFormat
) {
    public SystemConfiguration toDomain(String updatedBy) {
        return SystemConfiguration.builder()
                .id(1L)
                .maxFailedAttempts(maxFailedAttempts)
                .lockoutDurationMinutes(lockoutDurationMinutes)
                .enableAutoUnlock(enableAutoUnlock)
                .mfaPolicy(mfaPolicy)
                .minPasswordLength(minPasswordLength)
                .requireUppercase(requireUppercase)
                .requireLowercase(requireLowercase)
                .requireDigits(requireDigits)
                .requireSpecialChar(requireSpecialChar)
                .passwordExpireDays(passwordExpireDays)
                .sessionIdleTimeoutMinutes(sessionIdleTimeoutMinutes)
                .sessionMaxInactiveDays(sessionMaxInactiveDays)
                .maxConcurrentSessions(maxConcurrentSessions)
                .forceSingleSession(forceSingleSession)
                .accessTokenTtlHours(accessTokenTtlHours)
                .refreshTokenTtlDays(refreshTokenTtlDays)
                .authCodeTtlMinutes(authCodeTtlMinutes)
                .reuseRefreshTokens(reuseRefreshTokens)
                .tokenFormat(tokenFormat)
                .updatedAt(OffsetDateTime.now())
                .updatedBy(updatedBy)
                .build();
    }
}
