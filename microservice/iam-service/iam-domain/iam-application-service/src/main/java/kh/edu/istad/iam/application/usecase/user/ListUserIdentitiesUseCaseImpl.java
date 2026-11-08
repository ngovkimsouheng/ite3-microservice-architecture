package kh.edu.istad.iam.application.usecase.identity;

import kh.edu.istad.iam.application.dto.CommonDtos.IdentityResult;
import kh.edu.istad.iam.application.port.out.IdentityRepository;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.Identity;
import kh.edu.istad.iam.domain.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListUserIdentitiesUseCaseImpl implements ListUserIdentitiesUseCase {

    private final UserRepository userRepository;
    private final IdentityRepository identityRepository;

    @Override
    @Transactional(readOnly = true)
    public List<IdentityResult> execute(UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userUuid));

        List<Identity> identities = identityRepository.findByUserId(user.getId());

        return identities.stream()
                .filter(i -> !i.isDeleted())
                .map(i -> new IdentityResult(
                        i.getUuid(),
                        i.getProvider(),
                        i.getProviderSubject(),
                        i.getEmail(),
                        i.getDisplayName(),
                        i.getLinkedAt()
                ))
                .toList();
    }
}