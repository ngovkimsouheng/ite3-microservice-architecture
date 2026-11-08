package kh.edu.istad.iam.application.usecase.permission;

import kh.edu.istad.iam.application.dto.CommonDtos.PageResult;
import kh.edu.istad.iam.application.dto.CommonDtos.PermissionResult;
import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.domain.core.entity.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListPermissionsUseCaseImpl implements ListPermissionsUseCase {

    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<PermissionResult> execute(int page, int size, String sort) {
        PageResult<Permission> permissionPage = permissionRepository.findAll(page, size, sort);

        List<PermissionResult> content = permissionPage.content().stream()
                .map(p -> new PermissionResult(
                        p.getUuid(),
                        p.getName(),
                        p.getResource(),
                        p.getAction(),
                        p.getDescription(),
                        p.isSystem(),
                        p.isActive()
                ))
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