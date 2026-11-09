package kh.edu.istad.iam.domain.usecase.user;

import kh.edu.istad.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LockUserUseCase {

    private final UserRepository userRepository;
    private final CurrentActorProvider currentActorProvider;
    @Transactional
    public void execute(UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userUuid));

        user.setAccountNonLocked(false);
        user.setUpdatedAt(OffsetDateTime.now());
        user.setUpdatedBy(currentActorProvider.getCurrentActorSub());

        userRepository.save(user);
    }
}
