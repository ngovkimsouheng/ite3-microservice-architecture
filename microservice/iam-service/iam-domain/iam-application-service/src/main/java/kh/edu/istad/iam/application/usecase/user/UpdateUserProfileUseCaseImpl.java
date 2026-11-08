package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.UpdateUserProfileCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.UserResult;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class UpdateUserProfileUseCaseImpl implements UpdateUserProfileUseCase {

    private final UserRepository userRepository;
    private final CurrentActorProvider currentActorProvider;

    @Override
    @Transactional
    public UserResult execute(UpdateUserProfileCommand command) {
        User user = userRepository.findByUuid(command.userUuid())
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + command.userUuid()));

        if (command.givenName() != null && !command.givenName().isBlank()) {
            user.setGivenName(command.givenName());
        }
        if (command.nickname() != null) {
            user.setNickname(command.nickname());
        }
        if (command.phoneNumber() != null) {
            user.setPhoneNumber(command.phoneNumber());
        }
        if (command.locale() != null && !command.locale().isBlank()) {
            user.setLocale(command.locale());
        }

        user.setUpdatedAt(OffsetDateTime.now());
        user.setUpdatedBy(currentActorProvider.getCurrentActorSub());

        User updated = userRepository.save(user);

        return new UserResult(
                updated.getUuid(),
                updated.getUsername(),
                updated.getEmail(),
                updated.isEmailVerified(),
                updated.getGivenName(),
                updated.getFamilyName(),
                updated.getLocale(),
                updated.isEnabled(),
                updated.getCreatedAt(),
                updated.getUpdatedAt()
        );
    }
}