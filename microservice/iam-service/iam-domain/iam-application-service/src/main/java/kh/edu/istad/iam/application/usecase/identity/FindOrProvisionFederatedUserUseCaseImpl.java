package kh.edu.istad.iam.application.usecase.identity;

import kh.edu.istad.iam.application.dto.CommonDtos.AuthUserResult;
import kh.edu.istad.iam.application.dto.CommonDtos.FederatedIdentityCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.UserAuthoritiesResult;
import kh.edu.istad.iam.application.port.out.IdentityRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.Identity;
import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.application.usecase.user.GetUserAuthoritiesUseCase;
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
public class FindOrProvisionFederatedUserUseCaseImpl implements FindOrProvisionFederatedUserUseCase {

    private static final String FEDERATED_LOGIN_ACTOR = "FEDERATED_LOGIN";

    private final IdentityRepository identityRepository;
    private final UserRepository userRepository;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;

    @Override
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

        String email = command.email() == null ? null : command.email().trim().toLowerCase(Locale.ROOT);
        if (email == null || email.isBlank() || email.indexOf('@') <= 0 || email.endsWith("@")) {
            throw new IllegalArgumentException("A valid email address is required to create a federated user.");
        }
        if (!command.emailVerified()) {
            throw new IllegalArgumentException("A verified email address is required to create a federated user.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(
                    "This email is already registered. Sign in to the existing account before linking another provider."
            );
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

        identityRepository.save(Identity.builder()
                .uuid(UUID.randomUUID())
                .userId(user.getId())
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
                .build());

        return toAuthUserResult(user);
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
