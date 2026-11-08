//package kh.edu.istad.iam.persistence.repository;
//
//import kh.edu.istad.iam.persistence.entity.RolePermissionJpaEntity;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.Optional;
//
//public interface RolePermissionJpaRepository extends JpaRepository<RolePermissionJpaEntity, Long> {
//
//    @Query("SELECT r FROM RolePermissionJpaEntity r " +
//            "WHERE r.roleId = :roleId " +
//            "AND r.permissionId = :permissionId " +
//            "AND r.revokedAt IS NULL")
//    Optional<RolePermissionJpaEntity> findActive(@Param("roleId") Long roleId,
//                                                 @Param("permissionId") Long permissionId);
//}