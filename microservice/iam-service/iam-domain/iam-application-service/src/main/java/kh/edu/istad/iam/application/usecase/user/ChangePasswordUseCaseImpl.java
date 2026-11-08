package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.ChangePasswordCommand;
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
public class ChangePasswordUseCaseImpl implements ChangePasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    @Override
    @Transactional
    public void execute(UUID userUuid, ChangePasswordCommand command) {
        if (!command.newPassword().equals(command.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

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