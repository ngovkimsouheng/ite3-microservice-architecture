package kh.edu.istad.iam.domain.usecase.security;

import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.core.entity.UserSecurityEvent;
import kh.edu.istad.iam.domain.dto.UserSecurityEventResult;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import kh.edu.istad.iam.domain.port.out.UserSecurityEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListSecurityActivityUseCase {

    private final UserSecurityEventRepository repository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserSecurityEventResult> execute(UUID userUuid, int limit) {
        User user = userRepository.findByUuid(userUuid).orElse(null);
        if (user == null) {
            return Collections.emptyList();
        }

        List<UserSecurityEvent> events = repository.findUserActivities(user.getId(), limit);
        if (events.isEmpty()) {
            events = repository.findUserActivitiesByUsername(user.getUsername(), limit);
        }

        return events.stream().map(this::toResult).toList();
    }

    private UserSecurityEventResult toResult(UserSecurityEvent e) {
        return new UserSecurityEventResult(
                e.getUuid(),
                e.getUsername(),
                e.getCategory(),
                e.getEventType(),
                e.getStatus(),
                e.getIpAddress(),
                e.getDevice(),
                e.getLocation(),
                e.getAuthMethod(),
                e.getDescription(),
                e.getDetails(),
                e.getCreatedAt()
        );
    }
}
