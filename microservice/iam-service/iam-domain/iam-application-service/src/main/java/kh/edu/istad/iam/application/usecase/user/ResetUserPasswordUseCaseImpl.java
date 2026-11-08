package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.ResetUserPasswordCommand;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.PasswordHasher;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ResetUserPasswordUseCaseImpl implements ResetUserPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final CurrentActorProvider currentActorProvider;

    @Override
    @Transactional
    public void execute(ResetUserPasswordCommand command) {
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