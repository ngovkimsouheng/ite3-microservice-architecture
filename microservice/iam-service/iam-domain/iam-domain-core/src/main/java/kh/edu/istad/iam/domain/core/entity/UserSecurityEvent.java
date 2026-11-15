package kh.edu.istad.iam.domain.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSecurityEvent {
    private Long id;
    private UUID uuid;
    private Long userId;
    private String username;
    private String category; // 'LOGIN', 'ACTIVITY'
    private String eventType; // 'LOGIN_SUCCESS', 'LOGIN_FAILED', 'PASSWORD_CHANGED', etc.
    private String status; // 'SUCCESS', 'FAILED'
    private String ipAddress;
    private String device;
    private String location;
    private String authMethod;
    private String description;
    private String details;
    private OffsetDateTime createdAt;
}
