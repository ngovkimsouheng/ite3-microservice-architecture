package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.RegisterUserCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.UserResult;
import kh.edu.istad.iam.application.port.out.PasswordHasher;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    @Override
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

        return new UserResult(
                saved.getUuid(),
                saved.getUsername(),
                saved.getEmail(),
                saved.isEmailVerified(),
                saved.getGivenName(),
                saved.getFamilyName(),
                saved.getLocale(),
                saved.getPhoneNumber(),
                saved.isEnabled(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }
}
