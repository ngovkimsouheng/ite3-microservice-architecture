package kh.edu.istad.iam.domain.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfiguration {

    private Long id;

    // 1. Authentication Policies
    private Integer maxFailedAttempts;
    private Integer lockoutDurationMinutes;
    private Boolean enableAutoUnlock;
    private String mfaPolicy; // "OPTIONAL", "REQUIRED_ADMIN", "REQUIRED_ALL", "DISABLED"

    // 2. Password Policies
    private Integer minPasswordLength;
    private Boolean requireUppercase;
    private Boolean requireLowercase;
    private Boolean requireDigits;
    private Boolean requireSpecialChar;
    private Integer passwordExpireDays;

    // 3. Session Expiration Policies
    private Integer sessionIdleTimeoutMinutes;
    private Integer sessionMaxInactiveDays;
    private Integer maxConcurrentSessions;
    private Boolean forceSingleSession;

    // 4. Token Lifetimes & Settings
    private Integer accessTokenTtlHours;
    private Integer refreshTokenTtlDays;
    private Integer authCodeTtlMinutes;
    private Boolean reuseRefreshTokens;
    private String tokenFormat;

    private OffsetDateTime updatedAt;
    private String updatedBy;

    public static SystemConfiguration defaultConfiguration() {
        return SystemConfiguration.builder()
                .id(1L)
                .maxFailedAttempts(5)
                .lockoutDurationMinutes(30)
                .enableAutoUnlock(true)
                .mfaPolicy("OPTIONAL")
                .minPasswordLength(8)
                .requireUppercase(true)
                .requireLowercase(true)
                .requireDigits(true)
                .requireSpecialChar(true)
                .passwordExpireDays(90)
                .sessionIdleTimeoutMinutes(30)
                .sessionMaxInactiveDays(7)
                .maxConcurrentSessions(3)
                .forceSingleSession(false)
                .accessTokenTtlHours(5)
                .refreshTokenTtlDays(7)
                .authCodeTtlMinutes(3)
                .reuseRefreshTokens(false)
                .tokenFormat("JWT")
                .updatedAt(OffsetDateTime.now())
                .updatedBy("SYSTEM")
                .build();
    }
}
