package kh.edu.istad.iam.persistence.adapter;

import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.domain.core.entity.Permission;
import kh.edu.istad.iam.persistence.entity.PermissionJpaEntity;
import kh.edu.istad.iam.persistence.repository.PermissionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements PermissionRepository {

    private final PermissionJpaRepository permissionJpaRepository;

    @Override
    public Permission save(Permission p) {
        PermissionJpaEntity entity = PermissionJpaEntity.builder()
                .id(p.getId())
                .uuid(p.getUuid())
                .name(p.getName())
                .resource(p.getResource())
                .action(p.getAction())
                .description(p.getDescription())
                .isSystem(p.isSystem())
                .isActive(p.isActive())
                .isDeleted(p.isDeleted())
                .deletedAt(p.getDeletedAt())
                .deletedBy(p.getDeletedBy())
                .createdAt(p.getCreatedAt())
                .createdBy(p.getCreatedBy())
                .updatedAt(p.getUpdatedAt())
                .updatedBy(p.getUpdatedBy())
                .version(p.getVersion() != null ? p.getVersion() : 0L)
                .build();
        return mapToDomain(permissionJpaRepository.save(entity));
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return permissionJpaRepository.findById(id).map(this::mapToDomain);
    }

    @Override
    public Optional<Permission> findByUuid(UUID uuid) {
        return permissionJpaRepository.findByUuid(uuid).map(this::mapToDomain);
    }

    @Override
    public Optional<Permission> findByName(String name) {
        return permissionJpaRepository.findByName(name).map(this::mapToDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return permissionJpaRepository.findByName(name).isPresent();
    }

    @Override
    public List<Permission> findAllByIds(Iterable<Long> ids) {
        return permissionJpaRepository.findAllById(ids).stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public List<Permission> findAllActive() {
        return permissionJpaRepository.findAll().stream()
                .filter(p -> p.isActive() && !p.isDeleted())
                .map(this::mapToDomain)
                .toList();
    }

    private Permission mapToDomain(PermissionJpaEntity e) {
        return Permission.builder()
                .id(e.getId())
                .uuid(e.getUuid())
                .name(e.getName())
                .resource(e.getResource())
                .action(e.getAction())
                .description(e.getDescription())
                .isSystem(e.isSystem())
                .isActive(e.isActive())
                .isDeleted(e.isDeleted())
                .deletedAt(e.getDeletedAt())
                .deletedBy(e.getDeletedBy())
                .createdAt(e.getCreatedAt())
                .createdBy(e.getCreatedBy())
                .updatedAt(e.getUpdatedAt())
                .updatedBy(e.getUpdatedBy())
                .version(e.getVersion())
                .build();
    }
}