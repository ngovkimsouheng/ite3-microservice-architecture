package kh.edu.istad.iam.domain.usecase.role;

import kh.edu.istad.iam.domain.dto.PageResult;
import kh.edu.istad.iam.domain.dto.RoleSummary;
import kh.edu.istad.iam.domain.mapper.DomainDtoMapper;
import kh.edu.istad.iam.domain.port.out.RoleRepository;
import kh.edu.istad.iam.domain.core.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListRolesUseCase {

    private final RoleRepository roleRepository;
    private final DomainDtoMapper domainDtoMapper;

    @Transactional(readOnly = true)
    public PageResult<RoleSummary> execute(int page, int size, String sort) {
        PageResult<Role> rolePage = roleRepository.findAll(page, size, sort);
        List<RoleSummary> content = rolePage.content().stream()
                .map(domainDtoMapper::toRoleSummary)
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
