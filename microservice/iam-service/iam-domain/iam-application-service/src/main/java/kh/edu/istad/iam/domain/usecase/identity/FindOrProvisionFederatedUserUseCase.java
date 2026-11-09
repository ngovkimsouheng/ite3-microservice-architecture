package kh.edu.istad.iam.domain.usecase.identity;

import kh.edu.istad.iam.domain.dto.AuthUserResult;
import kh.edu.istad.iam.domain.dto.FederatedIdentityCommand;
import kh.edu.istad.iam.domain.dto.UserAuthoritiesResult;
import kh.edu.istad.iam.domain.port.out.IdentityRepository;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.Identity;
import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.core.valueobject.EmailAddress;
import kh.edu.istad.iam.domain.usecase.user.GetUserAuthoritiesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindOrProvisionFederatedUserUseCase {

    private static final String FEDERATED_LOGIN_ACTOR = "FEDERATED_LOGIN";

    private final IdentityRepository identityRepository;
    private final UserRepository userRepository;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    @Transactional
    public AuthUserResult execute(FederatedIdentityCommand command) {
        if (command == null || command.provider() == null || command.provider().isBlank()
                || command.providerSubject() == null || command.providerSubject().isBlank()) {
            throw new IllegalArgumentException("Federated provider and subject are required.");
        }

        String provider = command.provider().trim().toUpperCase(Locale.ROOT);
        String providerSubject = command.providerSubject().trim();

        Identity identity = identityRepository.findByProviderAndProviderSubject(provider, providerSubject)
                .orElse(null);
        if (identity != null) {
            if (identity.isDeleted()) {
                throw new IllegalStateException("The federated identity is no longer active.");
            }

            User user = userRepository.findById(identity.getUserId())
                    .orElseThrow(() -> new IllegalStateException("Federated identity has no associated user."));
            if (user.isDeleted() || !user.isEnabled()) {
                throw new IllegalStateException("The associated user account is not active.");
            }

            OffsetDateTime now = OffsetDateTime.now();
            identity.setLastLoginAt(now);
            identity.setUpdatedAt(now);
            identity.setUpdatedBy(FEDERATED_LOGIN_ACTOR);
            identityRepository.save(identity);
            user.setLastLoginAt(now);
            user.setUpdatedAt(now);
            user.setUpdatedBy(FEDERATED_LOGIN_ACTOR);
            user = userRepository.save(user);
            return toAuthUserResult(user);
        }

        String email;
        try {
            email = new EmailAddress(command.email()).value();
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "A valid email address is required to create a federated user.", exception);
        }
        if (!command.emailVerified()) {
            throw new IllegalArgumentException("A verified email address is required to create a federated user.");
        }
        User existingUser = userRepository.findByEmail(email).orElse(null);
        if (existingUser != null) {
            if (!existingUser.isEmailVerified()) {
                throw new IllegalArgumentException(
                        "This email belongs to an account that has not verified it. Verify the account email before using another provider."
                );
            }
            if (existingUser.isDeleted() || !existingUser.isEnabled()) {
                throw new IllegalStateException("The associated user account is not active.");
            }
            if (identityRepository.findActiveByUserIdAndProvider(existingUser.getId(), provider).isPresent()) {
                throw new IllegalArgumentException(
                        "An account for this provider is already linked to the existing user."
                );
            }

            OffsetDateTime now = OffsetDateTime.now();
            identityRepository.save(createIdentity(existingUser.getId(), provider, providerSubject, email, command, now));
            existingUser.setLastLoginAt(now);
            existingUser.setUpdatedAt(now);
            existingUser.setUpdatedBy(FEDERATED_LOGIN_ACTOR);
            return toAuthUserResult(userRepository.save(existingUser));
        }

        OffsetDateTime now = OffsetDateTime.now();
        UUID userUuid = UUID.randomUUID();
        String username = createUsername(command.username(), email);
        String[] names = splitDisplayName(command.displayName(), username);

        User user = userRepository.save(User.builder()
                .uuid(userUuid)
                .username(username)
                .email(email)
                .emailVerified(true)
                .givenName(names[0])
                .familyName(names[1])
                .pictureUrl(command.pictureUrl())
                .locale("en")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .failedLoginAttempts(0)
                .isDeleted(false)
                .lastLoginAt(now)
                .createdAt(now)
                .createdBy(FEDERATED_LOGIN_ACTOR)
                .updatedAt(now)
                .updatedBy(FEDERATED_LOGIN_ACTOR)
                .version(0L)
                .build());

        identityRepository.save(createIdentity(user.getId(), provider, providerSubject, email, command, now));

        return toAuthUserResult(user);
    }

    private Identity createIdentity(Long userId, String provider, String providerSubject, String email,
                                    FederatedIdentityCommand command, OffsetDateTime now) {
        return Identity.builder()
                .uuid(UUID.randomUUID())
                .userId(userId)
                .provider(provider)
                .providerSubject(providerSubject)
                .email(email)
                .emailVerified(true)
                .displayName(command.displayName())
                .username(command.username())
                .pictureUrl(command.pictureUrl())
                .attributes(command.attributes())
                .linkedAt(now)
                .lastLoginAt(now)
                .isDeleted(false)
                .createdAt(now)
                .createdBy(FEDERATED_LOGIN_ACTOR)
                .updatedAt(now)
                .updatedBy(FEDERATED_LOGIN_ACTOR)
                .version(0L)
                .build();
    }

    private String createUsername(String providerUsername, String email) {
        String candidate = providerUsername == null || providerUsername.isBlank()
                ? email.substring(0, email.indexOf('@'))
                : providerUsername.trim();
        candidate = candidate.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9._-]", "-")
                .replaceAll("^[._-]+|[._-]+$", "");
        if (candidate.isBlank()) {
            candidate = "user";
        }
        candidate = candidate.substring(0, Math.min(candidate.length(), 50));

        if (!userRepository.existsByUsername(candidate)) {
            return candidate;
        }

        String base = candidate.substring(0, Math.min(candidate.length(), 41));
        String uniqueCandidate;
        do {
            String suffix = "-" + UUID.randomUUID().toString().substring(0, 8);
            uniqueCandidate = base + suffix;
        } while (userRepository.existsByUsername(uniqueCandidate));
        return uniqueCandidate;
    }

    private String[] splitDisplayName(String displayName, String fallback) {
        String name = displayName == null || displayName.isBlank() ? fallback : displayName.trim();
        String[] parts = name.split("\\s+", 2);
        return new String[]{parts[0], parts.length > 1 ? parts[1] : ""};
    }

    private AuthUserResult toAuthUserResult(User user) {
        Set<String> authorities = new HashSet<>();
        UserAuthoritiesResult authorityResult = getUserAuthoritiesUseCase.execute(user.getUuid());
        if (authorityResult != null) {
            if (authorityResult.roles() != null) {
                authorities.addAll(authorityResult.roles());
            }
            if (authorityResult.permissions() != null) {
                authorities.addAll(authorityResult.permissions());
            }
        }

        return new AuthUserResult(
                user.getUuid(),
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isAccountNonLocked(),
                user.isCredentialsNonExpired(),
                authorities
        );
    }
}
