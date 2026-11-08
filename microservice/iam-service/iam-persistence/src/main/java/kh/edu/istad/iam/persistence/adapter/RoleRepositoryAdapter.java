package kh.edu.istad.iam.persistence.adapter;

import kh.edu.istad.iam.application.dto.CommonDtos;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.domain.core.entity.Role;
import kh.edu.istad.iam.domain.core.entity.RolePermissionAssignment;
import kh.edu.istad.iam.persistence.entity.RoleJpaEntity;
import kh.edu.istad.iam.persistence.entity.RolePermissionAssignmentJpaEntity;
import kh.edu.istad.iam.persistence.repository.RoleJpaRepository;
import kh.edu.istad.iam.persistence.repository.RolePermissionAssignmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleJpaRepository roleJpaRepository;
    private final RolePermissionAssignmentJpaRepository rolePermRepo;
    @Override
    public CommonDtos.PageResult<Role> findAll(int page, int size, String sort) {
        Sort sortObj;
        if (sort != null && !sort.isBlank()) {
            sortObj = Sort.by(sort);
        } else {
            sortObj = Sort.by("id").ascending();
        }
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<RoleJpaEntity> entityPage = roleJpaRepository.findAllByIsDeletedFalse(pageable);

        List<Role> content = entityPage.getContent().stream()
                .map(this::mapToDomain) // or your mapping method
                .toList();

        return new CommonDtos.PageResult<>(
                content,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages()
        );
    }
    @Override
    public Role save(Role role) {
        RoleJpaEntity entity = RoleJpaEntity.builder()
                .id(role.getId())
                .uuid(role.getUuid())
                .name(role.getName())
                .description(role.getDescription())
                .isSystem(role.isSystem())
                .isActive(role.isActive())
                .isDeleted(role.isDeleted())
                .deletedAt(role.getDeletedAt())
                .deletedBy(role.getDeletedBy())
                .createdAt(role.getCreatedAt())
                .createdBy(role.getCreatedBy())
                .updatedAt(role.getUpdatedAt())
                .updatedBy(role.getUpdatedBy())
                .version(role.getVersion() != null ? role.getVersion() : 0L)
                .build();
        return mapToDomain(roleJpaRepository.save(entity));
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleJpaRepository.findById(id).map(this::mapToDomain);
    }

    @Override
    public Optional<Role> findByUuid(UUID uuid) {
        return roleJpaRepository.findByUuid(uuid).map(this::mapToDomain);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleJpaRepository.findByName(name).map(this::mapToDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return roleJpaRepository.findByName(name).isPresent();
    }

    @Override
    public List<Role> findAllActive() {
        return roleJpaRepository.findAll().stream()
                .filter(r -> r.isActive() && !r.isDeleted())
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public void savePermissionAssignment(RolePermissionAssignment assignment) {
        rolePermRepo.save(RolePermissionAssignmentJpaEntity.builder()
                .id(assignment.getId())
                .roleId(assignment.getRoleId())
                .permissionId(assignment.getPermissionId())
                .assignedAt(assignment.getAssignedAt())
                .assignedBy(assignment.getAssignedBy())
                .expiresAt(assignment.getExpiresAt())
                .revokedAt(assignment.getRevokedAt())
                .revokedBy(assignment.getRevokedBy())
                .reason(assignment.getReason())
                .build());
    }

    @Override
    public List<RolePermissionAssignment> findPermissionAssignmentsByRoleId(Long roleId) {
        return rolePermRepo.findByRoleId(roleId).stream()
                .map(e -> RolePermissionAssignment.builder()
                        .id(e.getId())
                        .roleId(e.getRoleId())
                        .permissionId(e.getPermissionId())
                        .assignedAt(e.getAssignedAt())
                        .assignedBy(e.getAssignedBy())
                        .expiresAt(e.getExpiresAt())
                        .revokedAt(e.getRevokedAt())
                        .revokedBy(e.getRevokedBy())
                        .reason(e.getReason())
                        .build())
                .toList();
    }

    @Override
    public Optional<RolePermissionAssignment> findPermissionAssignment(Long roleId, Long permissionId) {
        return rolePermRepo.findByRoleIdAndPermissionId(roleId, permissionId)
                .map(e -> RolePermissionAssignment.builder()
                        .id(e.getId())
                        .roleId(e.getRoleId())
                        .permissionId(e.getPermissionId())
                        .assignedAt(e.getAssignedAt())
                        .assignedBy(e.getAssignedBy())
                        .expiresAt(e.getExpiresAt())
                        .revokedAt(e.getRevokedAt())
                        .revokedBy(e.getRevokedBy())
                        .reason(e.getReason())
                        .build());
    }

    private Role mapToDomain(RoleJpaEntity e) {
        return Role.builder()
                .id(e.getId())
                .uuid(e.getUuid())
                .name(e.getName())
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