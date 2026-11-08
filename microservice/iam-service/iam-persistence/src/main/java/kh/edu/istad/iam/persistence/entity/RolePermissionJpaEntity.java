//package kh.edu.istad.iam.persistence.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.OffsetDateTime;
//
//@Entity
//@Table(name = "role_perms")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class RolePermissionJpaEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "role_id", nullable = false)
//    private Long roleId;
//
//    @Column(name = "permission_id", nullable = false)
//    private Long permissionId;
//
//    @Column(name = "assigned_at", nullable = false)
//    private OffsetDateTime assignedAt;
//
//    @Column(name = "assigned_by")
//    private String assignedBy;
//
//    @Column(name = "expires_at")
//    private OffsetDateTime expiresAt;
//
//    @Column(name = "revoked_at")
//    private OffsetDateTime revokedAt;
//
//    @Column(name = "revoked_by")
//    private String revokedBy;
//
//    @Column(name = "reason")
//    private String reason;
//}