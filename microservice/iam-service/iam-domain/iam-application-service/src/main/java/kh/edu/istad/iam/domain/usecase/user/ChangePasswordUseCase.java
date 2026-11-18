package kh.edu.istad.iam.domain.usecase.user;

import kh.edu.istad.iam.domain.dto.ChangePasswordCommand;
import kh.edu.istad.iam.domain.port.out.PasswordHasher;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.service.PasswordPolicyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final PasswordPolicyValidator passwordPolicyValidator;

    @Transactional
    public void execute(UUID userUuid, ChangePasswordCommand command) {
        if (!command.newPassword().equals(command.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Validate against configured enterprise password policies
        passwordPolicyValidator.validate(command.newPassword());

        User user = userRepository.findByUuid(userUuid)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found with UUID: " + userUuid));

        if (!passwordHasher.matches(command.currentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid current password");
        }

        user.setPasswordHash(passwordHasher.hash(command.newPassword()));
        user.setPasswordChangedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        user.setUpdatedBy(userUuid.toString());

        userRepository.save(user);
    }
}
