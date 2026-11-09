package kh.edu.istad.iam.persistence.adapter;

import kh.edu.istad.iam.domain.dto.*;
import kh.edu.istad.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.iam.domain.core.entity.Permission;
import kh.edu.istad.iam.persistence.entity.PermissionJpaEntity;
import kh.edu.istad.iam.persistence.mapper.PermissionPersistenceMapper;
import kh.edu.istad.iam.persistence.repository.PermissionJpaRepository;
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
public class PermissionRepositoryAdapter implements PermissionRepository {

    private final PermissionJpaRepository permissionJpaRepository;
    private final PermissionPersistenceMapper permissionMapper;
    @Override
    public PageResult<Permission> findAll(int page, int size, String sort) {
        Sort sortObj = (sort != null && !sort.isBlank()) ? Sort.by(sort) : Sort.by("id").ascending();
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<PermissionJpaEntity> entityPage = permissionJpaRepository.findAllByIsDeletedFalse(pageable);

        List<Permission> domainList = entityPage.getContent().stream()
                .map(permissionMapper::toDomain)
                .toList();

        return new PageResult<>(
                domainList,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages()
        );
    }
    @Override
    public Permission save(Permission p) {
        return permissionMapper.toDomain(permissionJpaRepository.save(permissionMapper.toEntity(p)));
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return permissionJpaRepository.findById(id).map(permissionMapper::toDomain);
    }

    @Override
    public Optional<Permission> findByUuid(UUID uuid) {
        return permissionJpaRepository.findByUuid(uuid).map(permissionMapper::toDomain);
    }

    @Override
    public Optional<Permission> findByName(String name) {
        return permissionJpaRepository.findByName(name).map(permissionMapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return permissionJpaRepository.findByName(name).isPresent();
    }

    @Override
    public List<Permission> findAllByIds(Iterable<Long> ids) {
        return permissionJpaRepository.findAllById(ids).stream()
                .map(permissionMapper::toDomain)
                .toList();
    }

    @Override
    public List<Permission> findAllActive() {
        return permissionJpaRepository.findAll().stream()
                .filter(p -> p.isActive() && !p.isDeleted())
                .map(permissionMapper::toDomain)
                .toList();
    }
}
