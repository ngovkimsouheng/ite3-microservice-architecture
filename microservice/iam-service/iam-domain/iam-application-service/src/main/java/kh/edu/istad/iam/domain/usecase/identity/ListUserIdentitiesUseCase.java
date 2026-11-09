package kh.edu.istad.iam.domain.usecase.identity;

import kh.edu.istad.iam.domain.dto.IdentityResult;
import kh.edu.istad.iam.domain.mapper.DomainDtoMapper;
import kh.edu.istad.iam.domain.port.out.IdentityRepository;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.Identity;
import kh.edu.istad.iam.domain.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListUserIdentitiesUseCase {

    private final UserRepository userRepository;
    private final IdentityRepository identityRepository;
    private final DomainDtoMapper domainDtoMapper;

    @Transactional(readOnly = true)
    public List<IdentityResult> execute(UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .filter(currentUser -> !currentUser.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userUuid));

        List<Identity> identities = identityRepository.findByUserId(user.getId());

        return identities.stream()
                .filter(identity -> !identity.isDeleted())
                .map(domainDtoMapper::toIdentityResult)
                .toList();
    }
}
