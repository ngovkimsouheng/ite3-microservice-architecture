package kh.edu.istad.iam.application.usecase.role;

import kh.edu.istad.iam.application.dto.CommonDtos.PageResult;
import kh.edu.istad.iam.application.dto.CommonDtos.RoleSummary;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.domain.core.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListRolesUseCaseImpl implements ListRolesUseCase {

    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResult<RoleSummary> execute(int page, int size, String sort) {
        PageResult<Role> rolePage = roleRepository.findAll(page, size, sort);
        List<RoleSummary> content = rolePage.content().stream()
                .map(r -> new RoleSummary(
                        r.getUuid(),
                        r.getName(),
                        r.isActive()
                ))
                .toList();

        return new PageResult<>(
                content,
                rolePage.pageNumber(),
                rolePage.pageSize(),
                rolePage.totalElements(),
                rolePage.totalPages()
        );
    }
}