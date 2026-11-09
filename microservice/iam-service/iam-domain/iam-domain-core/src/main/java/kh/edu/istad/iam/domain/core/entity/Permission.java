package kh.edu.istad.iam.domain.core.entity;

import kh.edu.istad.iam.domain.core.exception.DomainRuleViolationException;
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
public class Permission {
    private Long id;
    private UUID uuid;
    private String name; // resource:action e.g. user:read
    private String resource;
    private String action;
    private String description;
    private boolean isSystem;
    private boolean isActive;
    private boolean isDeleted;
    private OffsetDateTime deletedAt;
    private String deletedBy;
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
    private Long version;

    // Rule: Authorities: permission name (resource:action)
    public String getAuthority() {
        return name;
    }

    // Rule: System permissions cannot be changed or deleted
    public void validateModifiable() {
        if (isSystem) {
            throw new DomainRuleViolationException("System permission '" + name + "' cannot be modified or deleted.");
        }
    }

    // Rule: Inactive permissions cannot be assigned
    public void validateAssignable() {
        if (!isActive || isDeleted) {
            throw new DomainRuleViolationException("Inactive or deleted permission '" + name + "' cannot be assigned.");
        }
    }
}
