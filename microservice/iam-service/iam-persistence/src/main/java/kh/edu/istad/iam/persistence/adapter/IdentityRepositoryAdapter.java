package kh.edu.istad.iam.persistence.adapter;

import kh.edu.istad.iam.application.port.out.IdentityRepository;
import kh.edu.istad.iam.domain.core.entity.Identity;
import kh.edu.istad.iam.persistence.entity.IdentityJpaEntity;
import kh.edu.istad.iam.persistence.repository.IdentityJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IdentityRepositoryAdapter implements IdentityRepository {

    private final IdentityJpaRepository identityJpaRepository;

    @Override
    public Identity save(Identity identity) {
        return mapToDomain(identityJpaRepository.save(mapToJpa(identity)));
    }

    @Override
    public Optional<Identity> findByUuid(UUID uuid) {
        return identityJpaRepository.findByUuid(uuid).map(this::mapToDomain);
    }

    @Override
    public Optional<Identity> findByProviderAndProviderSubject(String provider, String providerSubject) {
        return identityJpaRepository.findByProviderAndProviderSubject(provider, providerSubject).map(this::mapToDomain);
    }

    @Override
    public List<Identity> findActiveByUserId(Long userId) {
        return identityJpaRepository.findByUserIdAndIsDeletedFalse(userId).stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public Optional<Identity> findActiveByUserIdAndProvider(Long userId, String provider) {
        return identityJpaRepository.findByUserIdAndProviderAndIsDeletedFalse(userId, provider)
                .map(this::mapToDomain);
    }

    private IdentityJpaEntity mapToJpa(Identity d) {
        return IdentityJpaEntity.builder()
                .id(d.getId())
                .uuid(d.getUuid())
                .userId(d.getUserId())
                .provider(d.getProvider())
                .providerSubject(d.getProviderSubject())
                .email(d.getEmail())
                .emailVerified(d.isEmailVerified())
                .displayName(d.getDisplayName())
                .username(d.getUsername())
                .pictureUrl(d.getPictureUrl())
                .attributes(d.getAttributes())
                .linkedAt(d.getLinkedAt())
                .lastLoginAt(d.getLastLoginAt())
                .isDeleted(d.isDeleted())
                .deletedAt(d.getDeletedAt())
                .deletedBy(d.getDeletedBy())
                .createdAt(d.getCreatedAt())
                .createdBy(d.getCreatedBy())
                .updatedAt(d.getUpdatedAt())
                .updatedBy(d.getUpdatedBy())
                .version(d.getVersion() != null ? d.getVersion() : 0L)
                .build();
    }

    private Identity mapToDomain(IdentityJpaEntity e) {
        return Identity.builder()
                .id(e.getId())
                .uuid(e.getUuid())
                .userId(e.getUserId())
                .provider(e.getProvider())
                .providerSubject(e.getProviderSubject())
                .email(e.getEmail())
                .emailVerified(e.isEmailVerified())
                .displayName(e.getDisplayName())
                .username(e.getUsername())
                .pictureUrl(e.getPictureUrl())
                .attributes(e.getAttributes())
                .linkedAt(e.getLinkedAt())
                .lastLoginAt(e.getLastLoginAt())
                .isDeleted(e.isDeleted())
                .deletedAt(e.getDeletedAt())
                .deletedBy(e.getDeletedBy())
                .createdAt(e.getCreatedAt())
                .createdBy(e.getCreatedBy())
                .updatedAt(e.getUpdatedAt())
                .updatedBy(e.getUpdatedBy())
                .version(e.getVersion())
                .build();
    }
}