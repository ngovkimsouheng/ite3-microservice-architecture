package kh.edu.istad.iam.domain.usecase.identity;

import kh.edu.istad.iam.domain.port.out.CurrentActorProvider;
import kh.edu.istad.iam.domain.port.out.IdentityRepository;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.Identity;
import kh.edu.istad.iam.domain.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnlinkIdentityUseCase {

    private final UserRepository userRepository;
    private final IdentityRepository identityRepository;
    private final CurrentActorProvider currentActorProvider;
    @Transactional
    public void execute(UUID userUuid, UUID identityUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userUuid));

        List<Identity> activeIdentities = identityRepository.findActiveByUserId(user.getId());
        Identity identity = identityRepository.findByUuid(identityUuid)
                .filter(candidate -> candidate.getUserId().equals(user.getId()) && !candidate.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("Identity is not linked to this user."));
        user.validateCanUnlinkIdentity(activeIdentities, identityUuid);

        String actor = currentActorProvider.getCurrentActorSub();
        identity.setDeleted(true);
        identity.setDeletedAt(OffsetDateTime.now());
        identity.setDeletedBy(actor);

        identityRepository.save(identity);
    }
}
