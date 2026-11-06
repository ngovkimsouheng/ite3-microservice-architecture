package kh.edu.istad.iam.domain.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Identity {
    private Long id;
    private UUID uuid;
    private Long userId;
    private String provider; // GOOGLE, GITHUB, FACEBOOK, TELEGRAM
    private String providerSubject;
    private String email;
    private boolean emailVerified;
    private String displayName;
    private String username;
    private String pictureUrl;
    private Map<String, Object> attributes;
    private OffsetDateTime linkedAt;
    private OffsetDateTime lastLoginAt;
    private boolean isDeleted;
    private OffsetDateTime deletedAt;
    private String deletedBy;
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
    private Long version;
}