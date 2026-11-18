package kh.edu.istad.iam.persistence.adapter;

import kh.edu.istad.iam.domain.core.entity.UserSecurityEvent;
import kh.edu.istad.iam.domain.port.out.UserSecurityEventRepository;
import kh.edu.istad.iam.persistence.entity.UserSecurityEventJpaEntity;
import kh.edu.istad.iam.persistence.repository.UserSecurityEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserSecurityEventRepositoryAdapter implements UserSecurityEventRepository {

    private final UserSecurityEventJpaRepository repository;

    @Override
    public UserSecurityEvent save(UserSecurityEvent event) {
        UserSecurityEventJpaEntity entity = toEntity(event);
        UserSecurityEventJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<UserSecurityEvent> findUserLoginHistory(Long userId, String status, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        if (status == null || status.isBlank() || status.equalsIgnoreCase("ALL")) {
            return repository.findLoginHistoryAll(userId, "", pageRequest)
                    .stream().map(this::toDomain).toList();
        } else {
            return repository.findLoginHistoryByStatus(userId, "", status.toLowerCase(), pageRequest)
                    .stream().map(this::toDomain).toList();
        }
    }

    @Override
    public List<UserSecurityEvent> findUserLoginHistoryByUsername(String username, String status, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        String cleanUsername = username != null ? username : "";
        if (status == null || status.isBlank() || status.equalsIgnoreCase("ALL")) {
            return repository.findLoginHistoryAll(null, cleanUsername, pageRequest)
                    .stream().map(this::toDomain).toList();
        } else {
            return repository.findLoginHistoryByStatus(null, cleanUsername, status.toLowerCase(), pageRequest)
                    .stream().map(this::toDomain).toList();
        }
    }

    @Override
    public List<UserSecurityEvent> findUserActivities(Long userId, int limit) {
        return repository.findActivities(userId, "", PageRequest.of(0, limit))
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<UserSecurityEvent> findUserActivitiesByUsername(String username, int limit) {
        return repository.findActivities(null, username != null ? username : "", PageRequest.of(0, limit))
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<UserSecurityEvent> findAllLoginHistory(String status, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        if (status == null || status.isBlank() || status.equalsIgnoreCase("ALL")) {
            return repository.findAllSystemLoginHistory(pageRequest)
                    .stream().map(this::toDomain).toList();
        } else {
            return repository.findAllSystemLoginHistoryByStatus(status.toLowerCase(), pageRequest)
                    .stream().map(this::toDomain).toList();
        }
    }

    @Override
    public List<UserSecurityEvent> findAllActivities(int limit) {
        return repository.findAllSystemActivities(PageRequest.of(0, limit))
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<UserSecurityEvent> findDeactivationRequests(int limit) {
        return repository.findDeactivationRequests(PageRequest.of(0, limit))
                .stream().map(this::toDomain).toList();
    }

    private UserSecurityEventJpaEntity toEntity(UserSecurityEvent d) {
        return UserSecurityEventJpaEntity.builder()
                .id(d.getId())
                .uuid(d.getUuid())
                .userId(d.getUserId())
                .username(d.getUsername())
                .category(d.getCategory())
                .eventType(d.getEventType())
                .status(d.getStatus())
                .ipAddress(d.getIpAddress())
                .device(d.getDevice())
                .location(d.getLocation())
                .authMethod(d.getAuthMethod())
                .description(d.getDescription())
                .details(d.getDetails())
                .createdAt(d.getCreatedAt())
                .build();
    }

    private UserSecurityEvent toDomain(UserSecurityEventJpaEntity e) {
        return UserSecurityEvent.builder()
                .id(e.getId())
                .uuid(e.getUuid())
                .userId(e.getUserId())
                .username(e.getUsername())
                .category(e.getCategory())
                .eventType(e.getEventType())
                .status(e.getStatus())
                .ipAddress(e.getIpAddress())
                .device(e.getDevice())
                .location(e.getLocation())
                .authMethod(e.getAuthMethod())
                .description(e.getDescription())
                .details(e.getDetails())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
