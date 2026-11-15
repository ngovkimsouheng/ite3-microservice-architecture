package kh.edu.istad.iam.domain.usecase.security;

import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.core.entity.UserSecurityEvent;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import kh.edu.istad.iam.domain.port.out.UserSecurityEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordSecurityEventUseCase {

    private final UserSecurityEventRepository securityEventRepository;
    private final UserRepository userRepository;

    @Transactional
    public void execute(
            UUID userUuid,
            String username,
            String category,
            String eventType,
            String status,
            String ipAddress,
            String device,
            String location,
            String authMethod,
            String description,
            String details
    ) {
        Long userId = null;
        String resolvedUsername = username;

        if (userUuid != null) {
            var userOpt = userRepository.findByUuid(userUuid);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                userId = user.getId();
                if (resolvedUsername == null || resolvedUsername.isBlank()) {
                    resolvedUsername = user.getUsername();
                }
            }
        } else if (resolvedUsername != null && !resolvedUsername.isBlank()) {
            var userOpt = userRepository.findByUsername(resolvedUsername);
            if (userOpt.isEmpty()) {
                userOpt = userRepository.findByEmail(resolvedUsername);
            }
            if (userOpt.isPresent()) {
                userId = userOpt.get().getId();
                resolvedUsername = userOpt.get().getUsername();
            }
        }

        if (resolvedUsername == null || resolvedUsername.isBlank()) {
            resolvedUsername = "UNKNOWN";
        }

        UserSecurityEvent event = UserSecurityEvent.builder()
                .uuid(UUID.randomUUID())
                .userId(userId)
                .username(resolvedUsername)
                .category(category)
                .eventType(eventType)
                .status(status != null ? status.toUpperCase() : "SUCCESS")
                .ipAddress(ipAddress != null ? ipAddress : "127.0.0.1")
                .device(device != null ? device : "Unknown Device")
                .location(location != null ? location : "Phnom Penh, Cambodia")
                .authMethod(authMethod)
                .description(description)
                .details(details)
                .createdAt(OffsetDateTime.now())
                .build();

        securityEventRepository.save(event);
        log.info("Recorded security event: category={}, type={}, user={}", category, eventType, resolvedUsername);
    }
}
