package kh.edu.istad.iam.domain.usecase.identity;

import kh.edu.istad.iam.domain.core.entity.Identity;
import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.dto.FederatedIdentityCommand;
import kh.edu.istad.iam.domain.dto.IdentityResult;
import kh.edu.istad.iam.domain.mapper.DomainDtoMapper;
import kh.edu.istad.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.iam.domain.port.out.IdentityRepository;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LinkIdentityService implements LinkIdentityUseCase {

    private static final Set<String> SUPPORTED_PROVIDERS = Set.of("GOOGLE", "GITHUB", "FACEBOOK", "TELEGRAM");

    private final UserRepository userRepository;
    private final IdentityRepository identityRepository;
    private final CurrentActorProvider currentActorProvider;
    private final DomainDtoMapper domainDtoMapper;

    @Override
    @Transactional
    public IdentityResult execute(UUID userUuid, FederatedIdentityCommand command) {
        if (command == null || command.provider() == null || command.providerSubject() == null
                || command.providerSubject().isBlank()) {
            throw new IllegalArgumentException("A federated provider and subject are required.");
        }

        User user = userRepository.findByUuid(userUuid)
                .filter(candidate -> !candidate.isDeleted() && candidate.isEnabled())
                .orElseThrow(() -> new IllegalArgumentException("User not found or inactive: " + userUuid));
        String provider = command.provider().trim().toUpperCase(Locale.ROOT);
        if (!SUPPORTED_PROVIDERS.contains(provider)) {
            throw new IllegalArgumentException("Unsupported identity provider: " + provider);
        }
        String providerSubject = command.providerSubject().trim();
        String actor = currentActorProvider.getCurrentActorSub();

        Identity existing = identityRepository.findByProviderAndProviderSubject(provider, providerSubject)
                .orElse(null);
        if (existing != null && !existing.getUserId().equals(user.getId())) {
            throw new IllegalStateException("This provider account is already linked to another user.");
        }
        if (existing != null && !existing.isDeleted()) {
            return domainDtoMapper.toIdentityResult(existing);
        }

        var identityForProvider = identityRepository.findActiveByUserIdAndProvider(user.getId(), provider);
        if (identityForProvider.isPresent()
                && !identityForProvider.get().getProviderSubject().equals(providerSubject)) {
            throw new IllegalStateException("Only one account per provider can be linked to a user.");
        }

        OffsetDateTime now = OffsetDateTime.now();
        Identity identity = existing == null
                ? Identity.builder().uuid(UUID.randomUUID()).userId(user.getId())
                        .provider(provider).providerSubject(providerSubject)
                        .createdAt(now).createdBy(actor).build()
                : existing;
        identity.setEmail(command.email());
        identity.setEmailVerified(command.emailVerified());
        identity.setDisplayName(command.displayName());
        identity.setUsername(command.username());
        identity.setPictureUrl(command.pictureUrl());
        identity.setAttributes(command.attributes());
        identity.setLinkedAt(now);
        identity.setLastLoginAt(now);
        identity.setDeleted(false);
        identity.setDeletedAt(null);
        identity.setDeletedBy(null);
        identity.setUpdatedAt(now);
        identity.setUpdatedBy(actor);
        return domainDtoMapper.toIdentityResult(identityRepository.save(identity));
    }
}
