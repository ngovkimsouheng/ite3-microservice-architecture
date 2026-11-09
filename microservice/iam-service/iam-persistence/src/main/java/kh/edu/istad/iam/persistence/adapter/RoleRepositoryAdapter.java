package kh.edu.istad.iam.persistence.adapter;

import kh.edu.istad.iam.domain.dto.*;
import kh.edu.istad.iam.domain.port.out.RoleRepository;
import kh.edu.istad.iam.domain.core.entity.Role;
import kh.edu.istad.iam.domain.core.entity.RolePermissionAssignment;
import kh.edu.istad.iam.persistence.entity.RoleJpaEntity;
import kh.edu.istad.iam.persistence.mapper.RolePersistenceMapper;
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
    private final RolePersistenceMapper roleMapper;
    @Override
    public PageResult<Role> findAll(int page, int size, String sort) {
        Sort sortObj;
        if (sort != null && !sort.isBlank()) {
            sortObj = Sort.by(sort);
        } else {
            sortObj = Sort.by("id").ascending();
        }
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<RoleJpaEntity> entityPage = roleJpaRepository.findAllByIsDeletedFalse(pageable);

        List<Role> content = entityPage.getContent().stream()
                .map(roleMapper::toDomain)
                .toList();

        return new PageResult<>(
                content,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages()
        );
    }
    @Override
    public Role save(Role role) {
        return roleMapper.toDomain(roleJpaRepository.save(roleMapper.toEntity(role)));
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleJpaRepository.findById(id).map(roleMapper::toDomain);
    }

    @Override
    public Optional<Role> findByUuid(UUID uuid) {
        return roleJpaRepository.findByUuid(uuid).map(roleMapper::toDomain);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleJpaRepository.findByName(name).map(roleMapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return roleJpaRepository.findByName(name).isPresent();
    }

    @Override
    public List<Role> findAllActive() {
        return roleJpaRepository.findAll().stream()
                .filter(r -> r.isActive() && !r.isDeleted())
                .map(roleMapper::toDomain)
                .toList();
    }

    @Override
    public void savePermissionAssignment(RolePermissionAssignment assignment) {
        rolePermRepo.save(roleMapper.toEntity(assignment));
    }

    @Override
    public List<RolePermissionAssignment> findPermissionAssignmentsByRoleId(Long roleId) {
        return rolePermRepo.findByRoleId(roleId).stream()
                .map(roleMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<RolePermissionAssignment> findPermissionAssignment(Long roleId, Long permissionId) {
        return rolePermRepo.findByRoleIdAndPermissionId(roleId, permissionId)
                .map(roleMapper::toDomain);
    }
}
