package kh.edu.istad.iam.domain.usecase.user;

import kh.edu.istad.iam.domain.dto.UpdateUserProfileCommand;
import kh.edu.istad.iam.domain.dto.UserResult;
import kh.edu.istad.iam.domain.mapper.DomainDtoMapper;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import kh.edu.istad.iam.domain.core.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class UpdateUserProfileUseCase {

    private final UserRepository userRepository;
    private final DomainDtoMapper domainDtoMapper;

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
            user.setPhoneNumber(command.phoneNumber().isBlank() ? null : command.phoneNumber().trim());
        }
        if (command.locale() != null && !command.locale().isBlank()) {
            user.setLocale(command.locale());
        }

        user.setUpdatedAt(OffsetDateTime.now());
        User saved = userRepository.save(user);

        return domainDtoMapper.toUserResult(saved);
    }
}
