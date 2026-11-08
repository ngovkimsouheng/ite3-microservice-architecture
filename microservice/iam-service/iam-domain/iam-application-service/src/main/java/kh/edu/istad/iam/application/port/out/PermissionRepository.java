package kh.edu.istad.iam.application.port.out;

import kh.edu.istad.iam.application.dto.CommonDtos.PageResult;
import kh.edu.istad.iam.domain.core.entity.Permission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository {
    Permission save(Permission permission);
    Optional<Permission> findById(Long id);
    Optional<Permission> findByUuid(UUID uuid);
    Optional<Permission> findByName(String name);
    boolean existsByName(String name);
    List<Permission> findAllByIds(Iterable<Long> ids);
    List<Permission> findAllActive();
    PageResult<Permission> findAll(int page, int size, String sort);
}