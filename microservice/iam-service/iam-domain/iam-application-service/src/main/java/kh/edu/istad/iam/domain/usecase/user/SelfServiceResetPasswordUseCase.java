package kh.edu.istad.iam.domain.usecase.user;

import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.dto.SelfServiceResetPasswordCommand;
import kh.edu.istad.iam.domain.port.out.PasswordHasher;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class SelfServiceResetPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    @Transactional
    public User execute(SelfServiceResetPasswordCommand command) {
        if (command.username() == null || command.username().isBlank()) {
            throw new IllegalArgumentException("Username or account identifier is required.");
        }
        if (command.email() == null || command.email().isBlank()) {
            throw new IllegalArgumentException("Registered email address is required for verification.");
        }
        if (command.newPassword() == null || command.newPassword().length() < 8) {
            throw new IllegalArgumentException("New password must be at least 8 characters long.");
        }
        if (!command.newPassword().equals(command.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        String search = command.username().trim();
        User user = userRepository.findByUsername(search)
                .or(() -> userRepository.findByEmail(search))
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("No account found matching username: " + search));

        String inputEmail = command.email().trim();
        if (user.getEmail() == null || !user.getEmail().equalsIgnoreCase(inputEmail)) {
            throw new IllegalArgumentException("The provided email does not match the registered account email.");
        }

        user.setPasswordHash(passwordHasher.hash(command.newPassword()));
        user.setPasswordChangedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        user.setUpdatedBy("SELF_SERVICE_RESET");

        return userRepository.save(user);
    }
}
