package kh.edu.istad.iam.persistence.repository;

import kh.edu.istad.iam.persistence.entity.PermissionJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PermissionJpaRepository extends JpaRepository<PermissionJpaEntity, Long> {
    Optional<PermissionJpaEntity> findByUuid(UUID uuid);
    Optional<PermissionJpaEntity> findByName(String name);
    Page<PermissionJpaEntity> findAllByIsDeletedFalse(Pageable pageable);
}