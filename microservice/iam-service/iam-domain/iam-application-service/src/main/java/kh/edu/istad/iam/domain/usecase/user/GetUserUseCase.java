package kh.edu.istad.iam.domain.usecase.user;

import kh.edu.istad.iam.domain.dto.UserResult;
import kh.edu.istad.iam.domain.mapper.DomainDtoMapper;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserUseCase {

    private final UserRepository userRepository;
    private final DomainDtoMapper domainDtoMapper;

    public UserResult execute(UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userUuid));

        return domainDtoMapper.toUserResult(user);
    }
}
