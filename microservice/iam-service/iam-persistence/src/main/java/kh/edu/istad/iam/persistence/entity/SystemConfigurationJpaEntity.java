package kh.edu.istad.iam.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "system_configurations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfigurationJpaEntity {

    @Id
    private Long id;

    // 1. Authentication Policies
    @Column(name = "max_failed_attempts", nullable = false)
    private Integer maxFailedAttempts;

    @Column(name = "lockout_duration_minutes", nullable = false)
    private Integer lockoutDurationMinutes;

    @Column(name = "enable_auto_unlock", nullable = false)
    private Boolean enableAutoUnlock;

    @Column(name = "mfa_policy", nullable = false, length = 64)
    private String mfaPolicy;

    // 2. Password Policies
    @Column(name = "min_password_length", nullable = false)
    private Integer minPasswordLength;

    @Column(name = "require_uppercase", nullable = false)
    private Boolean requireUppercase;

    @Column(name = "require_lowercase", nullable = false)
    private Boolean requireLowercase;

    @Column(name = "require_digits", nullable = false)
    private Boolean requireDigits;

    @Column(name = "require_special_char", nullable = false)
    private Boolean requireSpecialChar;

    @Column(name = "password_expire_days", nullable = false)
    private Integer passwordExpireDays;

    // 3. Session Expiration Policies
    @Column(name = "session_idle_timeout_minutes", nullable = false)
    private Integer sessionIdleTimeoutMinutes;

    @Column(name = "session_max_inactive_days", nullable = false)
    private Integer sessionMaxInactiveDays;

    @Column(name = "max_concurrent_sessions", nullable = false)
    private Integer maxConcurrentSessions;

    @Column(name = "force_single_session", nullable = false)
    private Boolean forceSingleSession;

    // 4. Token Lifetimes & Settings
    @Column(name = "access_token_ttl_hours", nullable = false)
    private Integer accessTokenTtlHours;

    @Column(name = "refresh_token_ttl_days", nullable = false)
    private Integer refreshTokenTtlDays;

    @Column(name = "auth_code_ttl_minutes", nullable = false)
    private Integer authCodeTtlMinutes;

    @Column(name = "reuse_refresh_tokens", nullable = false)
    private Boolean reuseRefreshTokens;

    @Column(name = "token_format", nullable = false, length = 64)
    private String tokenFormat;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "updated_by", length = 128)
    private String updatedBy;
}
