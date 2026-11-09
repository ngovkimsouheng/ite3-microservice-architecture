package kh.edu.istad.iam.domain.usecase.permission;

import kh.edu.istad.iam.domain.dto.PageResult;
import kh.edu.istad.iam.domain.dto.PermissionResult;
import kh.edu.istad.iam.domain.mapper.DomainDtoMapper;
import kh.edu.istad.iam.domain.port.out.PermissionRepository;
import kh.edu.istad.iam.domain.core.entity.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListPermissionsUseCase {

    private final PermissionRepository permissionRepository;
    private final DomainDtoMapper domainDtoMapper;

    @Transactional(readOnly = true)
    public PageResult<PermissionResult> execute(int page, int size, String sort) {
        PageResult<Permission> permissionPage = permissionRepository.findAll(page, size, sort);

        List<PermissionResult> content = permissionPage.content().stream()
                .map(domainDtoMapper::toPermissionResult)
                .toList();

        return new PageResult<>(
                content,
                permissionPage.pageNumber(),
                permissionPage.pageSize(),
                permissionPage.totalElements(),
                permissionPage.totalPages()
        );
    }
}
