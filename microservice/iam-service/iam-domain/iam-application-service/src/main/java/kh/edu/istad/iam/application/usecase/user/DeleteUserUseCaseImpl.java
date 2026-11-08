package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {

    private final UserRepository userRepository;
    private final CurrentActorProvider currentActorProvider;

    @Override
    @Transactional
    public void execute(UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userUuid));

        user.setDeleted(true);
        user.setDeletedAt(OffsetDateTime.now());
        user.setDeletedBy(currentActorProvider.getCurrentActorSub());

        userRepository.save(user);
    }
}