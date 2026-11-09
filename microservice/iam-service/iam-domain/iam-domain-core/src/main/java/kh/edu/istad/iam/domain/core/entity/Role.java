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
public class Role {
    private Long id;
    private UUID uuid;
    private String name;
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

    // Rule: Authorities format ROLE_ + name
    public String getAuthority() {
        return "ROLE_" + name;
    }

    // Rule: System roles cannot be changed or deleted
    public void validateModifiable() {
        if (isSystem) {
            throw new DomainRuleViolationException("System role '" + name + "' cannot be modified or deleted.");
        }
    }

    // Rule: Inactive roles cannot be assigned
    public void validateAssignable() {
        if (!isActive || isDeleted) {
            throw new DomainRuleViolationException("Inactive or deleted role '" + name + "' cannot be assigned.");
        }
    }
}
