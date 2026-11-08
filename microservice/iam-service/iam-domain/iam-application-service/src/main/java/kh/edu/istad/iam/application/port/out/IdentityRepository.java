package kh.edu.istad.iam.application.port.out;

import kh.edu.istad.iam.domain.core.entity.Identity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IdentityRepository {
    Identity save(Identity identity);
    Optional<Identity> findByUuid(UUID uuid);
    Optional<Identity> findByProviderAndProviderSubject(String provider, String providerSubject);
    List<Identity> findActiveByUserId(Long userId);
    Optional<Identity> findActiveByUserIdAndProvider(Long userId, String provider);
    List<Identity> findByUserId(Long userId);
}