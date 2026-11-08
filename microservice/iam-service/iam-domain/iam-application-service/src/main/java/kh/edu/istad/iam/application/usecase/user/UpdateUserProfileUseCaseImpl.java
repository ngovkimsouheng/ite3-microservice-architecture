package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.UpdateUserProfileCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.UserResult;
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

    @Override
    @Transactional
    public UserResult execute(UpdateUserProfileCommand command) {
        User user = userRepository.findByUuid(command.userUuid())
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + command.userUuid()));

        if (command.username() != null && !command.username().isBlank()) {
            if (!command.username().equals(user.getUsername())
                    && userRepository.findByUsername(command.username())
                    .filter(existing -> !existing.getUuid().equals(user.getUuid()))
                    .isPresent()) {
                throw new IllegalArgumentException("Username already taken.");
            }
            user.setUsername(command.username());
        }
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
        User saved = userRepository.save(user);

        return toUserResult(saved);
    }

    private UserResult toUserResult(User u) {
        return new UserResult(
                u.getUuid(),
                u.getUsername(),
                u.getEmail(),
                u.isEmailVerified(),
                u.getGivenName(),
                u.getFamilyName(),
                u.getLocale(),
                u.getPhoneNumber(),
                u.isEnabled(),
                u.getCreatedAt(),
                u.getUpdatedAt()
        );
    }
}