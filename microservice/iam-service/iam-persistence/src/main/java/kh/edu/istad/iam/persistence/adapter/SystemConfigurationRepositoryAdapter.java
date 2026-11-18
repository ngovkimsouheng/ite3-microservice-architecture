package kh.edu.istad.iam.persistence.adapter;

import kh.edu.istad.iam.domain.core.entity.SystemConfiguration;
import kh.edu.istad.iam.domain.port.out.SystemConfigurationRepository;
import kh.edu.istad.iam.persistence.entity.SystemConfigurationJpaEntity;
import kh.edu.istad.iam.persistence.repository.SystemConfigurationJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class SystemConfigurationRepositoryAdapter implements SystemConfigurationRepository {

    private final SystemConfigurationJpaRepository repository;

    @Override
    public SystemConfiguration getConfiguration() {
        return repository.findById(1L)
                .map(this::toDomain)
                .orElseGet(() -> {
                    SystemConfiguration defaults = SystemConfiguration.defaultConfiguration();
                    saveConfiguration(defaults);
                    return defaults;
                });
    }

    @Override
    public SystemConfiguration saveConfiguration(SystemConfiguration config) {
        if (config.getId() == null) {
            config.setId(1L);
        }
        if (config.getUpdatedAt() == null) {
            config.setUpdatedAt(OffsetDateTime.now());
        }
        SystemConfigurationJpaEntity entity = toEntity(config);
        SystemConfigurationJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    private SystemConfiguration toDomain(SystemConfigurationJpaEntity entity) {
        return SystemConfiguration.builder()
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

    private SystemConfigurationJpaEntity toEntity(SystemConfiguration domain) {
        return SystemConfigurationJpaEntity.builder()
                .id(domain.getId() != null ? domain.getId() : 1L)
                .maxFailedAttempts(domain.getMaxFailedAttempts() != null ? domain.getMaxFailedAttempts() : 5)
                .lockoutDurationMinutes(domain.getLockoutDurationMinutes() != null ? domain.getLockoutDurationMinutes() : 30)
                .enableAutoUnlock(domain.getEnableAutoUnlock() != null ? domain.getEnableAutoUnlock() : Boolean.TRUE)
                .mfaPolicy(domain.getMfaPolicy() != null ? domain.getMfaPolicy() : "OPTIONAL")
                .minPasswordLength(domain.getMinPasswordLength() != null ? domain.getMinPasswordLength() : 8)
                .requireUppercase(domain.getRequireUppercase() != null ? domain.getRequireUppercase() : Boolean.TRUE)
                .requireLowercase(domain.getRequireLowercase() != null ? domain.getRequireLowercase() : Boolean.TRUE)
                .requireDigits(domain.getRequireDigits() != null ? domain.getRequireDigits() : Boolean.TRUE)
                .requireSpecialChar(domain.getRequireSpecialChar() != null ? domain.getRequireSpecialChar() : Boolean.TRUE)
                .passwordExpireDays(domain.getPasswordExpireDays() != null ? domain.getPasswordExpireDays() : 90)
                .sessionIdleTimeoutMinutes(domain.getSessionIdleTimeoutMinutes() != null ? domain.getSessionIdleTimeoutMinutes() : 30)
                .sessionMaxInactiveDays(domain.getSessionMaxInactiveDays() != null ? domain.getSessionMaxInactiveDays() : 7)
                .maxConcurrentSessions(domain.getMaxConcurrentSessions() != null ? domain.getMaxConcurrentSessions() : 3)
                .forceSingleSession(domain.getForceSingleSession() != null ? domain.getForceSingleSession() : Boolean.FALSE)
                .accessTokenTtlHours(domain.getAccessTokenTtlHours() != null ? domain.getAccessTokenTtlHours() : 5)
                .refreshTokenTtlDays(domain.getRefreshTokenTtlDays() != null ? domain.getRefreshTokenTtlDays() : 7)
                .authCodeTtlMinutes(domain.getAuthCodeTtlMinutes() != null ? domain.getAuthCodeTtlMinutes() : 3)
                .reuseRefreshTokens(domain.getReuseRefreshTokens() != null ? domain.getReuseRefreshTokens() : Boolean.FALSE)
                .tokenFormat(domain.getTokenFormat() != null ? domain.getTokenFormat() : "JWT")
                .updatedAt(domain.getUpdatedAt() != null ? domain.getUpdatedAt() : OffsetDateTime.now())
                .updatedBy(domain.getUpdatedBy() != null ? domain.getUpdatedBy() : "SYSTEM")
                .build();
    }
}
