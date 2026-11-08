package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.UserResult;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserUseCaseImpl implements GetUserUseCase {

    private final UserRepository userRepository;

    @Override
    public UserResult execute(UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userUuid));

        return new UserResult(
                user.getUuid(),
                user.getUsername(),
                user.getEmail(),
                user.isEmailVerified(),
                user.getGivenName(),
                user.getFamilyName(),
                user.getLocale(),
                user.getPhoneNumber(),
                user.isEnabled(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
