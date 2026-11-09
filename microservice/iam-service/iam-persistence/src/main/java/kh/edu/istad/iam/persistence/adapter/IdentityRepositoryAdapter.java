package kh.edu.istad.iam.persistence.adapter;

import kh.edu.istad.iam.domain.port.out.IdentityRepository;
import kh.edu.istad.iam.domain.core.entity.Identity;
import kh.edu.istad.iam.persistence.mapper.IdentityPersistenceMapper;
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
    private final IdentityPersistenceMapper identityMapper;

    @Override
    public Identity save(Identity identity) {
        return identityMapper.toDomain(identityJpaRepository.save(identityMapper.toEntity(identity)));
    }

    @Override
    public Optional<Identity> findByUuid(UUID uuid) {
        return identityJpaRepository.findByUuid(uuid).map(identityMapper::toDomain);
    }

    @Override
    public Optional<Identity> findByProviderAndProviderSubject(String provider, String providerSubject) {
        return identityJpaRepository.findByProviderAndProviderSubject(provider, providerSubject).map(identityMapper::toDomain);
    }

    @Override
    public List<Identity> findActiveByUserId(Long userId) {
        return identityJpaRepository.findByUserIdAndIsDeletedFalse(userId).stream()
                .map(identityMapper::toDomain)
                .toList();
    }
    @Override
    public List<Identity> findByUserId(Long userId) {
        return identityJpaRepository.findByUserIdAndIsDeletedFalse(userId)
                .stream()
                .map(identityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Identity> findActiveByUserIdAndProvider(Long userId, String provider) {
        return identityJpaRepository.findByUserIdAndProviderAndIsDeletedFalse(userId, provider)
                .map(identityMapper::toDomain);
    }
}
