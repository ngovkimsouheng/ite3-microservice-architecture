package kh.edu.istad.iam.domain.usecase.user;

import kh.edu.istad.iam.domain.dto.RegisterUserCommand;
import kh.edu.istad.iam.domain.dto.UserResult;
import kh.edu.istad.iam.domain.mapper.DomainDtoMapper;
import kh.edu.istad.iam.domain.port.out.PasswordHasher;
import kh.edu.istad.iam.domain.port.out.RoleRepository;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.core.entity.UserRoleAssignment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final DomainDtoMapper domainDtoMapper;
    private final RoleRepository roleRepository;
    @Transactional
    public UserResult execute(RegisterUserCommand command) {
        if (!command.password().equals(command.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
        if (userRepository.existsByEmail(command.email().toLowerCase())) {
            throw new IllegalArgumentException("Email already registered.");
        }
        if (userRepository.existsByUsername(command.username())) {
            throw new IllegalArgumentException("Username already taken.");
        }

        OffsetDateTime now = OffsetDateTime.now();
        User user = User.builder()
                .uuid(UUID.randomUUID())
                .username(command.username())
                .email(command.email().toLowerCase())
                .emailVerified(false)
                .passwordHash(passwordHasher.hash(command.password()))
                .givenName(command.givenName())
                .familyName(command.familyName())
                .locale(command.locale())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .failedLoginAttempts(0)
                .isDeleted(false)
                .createdAt(now)
                .createdBy("SELF_REGISTER")
                .updatedAt(now)
                .updatedBy("SELF_REGISTER")
                .version(0L)
                .build();

        User saved = userRepository.save(user);
        var defaultRole = roleRepository.findByName("USER")
                .filter(role -> role.isActive() && !role.isDeleted())
                .orElseThrow(() -> new IllegalStateException("The system USER role is not configured."));
        userRepository.saveRoleAssignment(UserRoleAssignment.builder()
                .userId(saved.getId())
                .roleId(defaultRole.getId())
                .assignedAt(now)
                .assignedBy("SELF_REGISTER")
                .reason("Default registration role")
                .build());

        return domainDtoMapper.toUserResult(saved);
    }
}
