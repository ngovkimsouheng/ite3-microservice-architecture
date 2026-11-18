package kh.edu.istad.iam.domain.usecase.user;

import kh.edu.istad.iam.domain.dto.ResetUserPasswordCommand;
import kh.edu.istad.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.iam.domain.port.out.PasswordHasher;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.service.PasswordPolicyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ResetUserPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final CurrentActorProvider currentActorProvider;
    private final PasswordPolicyValidator passwordPolicyValidator;

    @Transactional
    public void execute(ResetUserPasswordCommand command) {
        // Validate against configured enterprise password policies
        passwordPolicyValidator.validate(command.newPassword());

        User user = userRepository.findByUuid(command.userUuid())
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + command.userUuid()));

        user.setPasswordHash(passwordHasher.hash(command.newPassword()));
        user.setPasswordChangedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        user.setUpdatedBy(currentActorProvider.getCurrentActorSub());

        userRepository.save(user);
    }
}
