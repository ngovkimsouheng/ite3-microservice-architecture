
package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.CreateUserCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.UserResult;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.PasswordHasher;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.Role;
import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.core.entity.UserRoleAssignment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHasher passwordHasher;
    private final CurrentActorProvider currentActorProvider;

    @Override
    @Transactional
    public UserResult execute(CreateUserCommand command) {

        // 1. Normalize and validate user information
        String email = command.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByUsername(command.username())) {
            throw new IllegalArgumentException("Username already taken.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered.");
        }

        if (command.phoneNumber() != null
                && !command.phoneNumber().isBlank()
                && userRepository.existsByPhoneNumber(command.phoneNumber())) {
            throw new IllegalArgumentException("Phone number already registered.");
        }
        // 2. Get current authenticated actor
        String actor = currentActorProvider.getCurrentActorSub();

        OffsetDateTime now = OffsetDateTime.now();

        // 3. Resolve and validate requested roles
        List<Role> roles = command.roleUuids() == null
                ? List.of()
                : new LinkedHashSet<>(command.roleUuids()).stream()
                .map(roleUuid -> {
                    Role role = roleRepository.findByUuid(roleUuid)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Role not found: " + roleUuid
                                    )
                            );

                    role.validateAssignable();
                    return role;
                })
                .toList();

        // 4. Create user domain entity
        User user = User.builder()
                .uuid(UUID.randomUUID())
                .username(command.username())
                .email(email)
                .emailVerified(false)
                .passwordHash(passwordHasher.hash(command.password()))
                .givenName(command.givenName())
                .familyName(command.familyName())
                .gender(command.gender())
                .birthdate(command.birthdate())
                .phoneNumber(command.phoneNumber())
                .phoneNumberVerified(false)
                .zoneinfo(command.zoneinfo())
                .locale(command.locale())
                .addressStreetAddress(command.addressStreetAddress())
                .addressLocality(command.addressLocality())
                .addressRegion(command.addressRegion())
                .addressPostalCode(command.addressPostalCode())
                .addressCountry(command.addressCountry())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .failedLoginAttempts(0)
                .isDeleted(false)
                .createdAt(now)
                .createdBy(actor)
                .updatedAt(now)
                .updatedBy(actor)
                .version(0L)
                .build();

        // 5. Save user first to obtain database ID
        User savedUser = userRepository.save(user);

        // 6. Assign requested roles
        for (Role role : roles) {

            UserRoleAssignment assignment = UserRoleAssignment.builder()
                    .userId(savedUser.getId())
                    .roleId(role.getId())
                    .assignedAt(now)
                    .assignedBy(actor)
                    .build();

            userRepository.saveRoleAssignment(assignment);
        }

        // 7. Return user result
        return new UserResult(
                savedUser.getUuid(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.isEmailVerified(),
                savedUser.getGivenName(),
                savedUser.getFamilyName(),
                savedUser.getLocale(),
                savedUser.getPhoneNumber(),
                savedUser.isEnabled(),
                savedUser.getCreatedAt(),
                savedUser.getUpdatedAt()
        );
    }
}
