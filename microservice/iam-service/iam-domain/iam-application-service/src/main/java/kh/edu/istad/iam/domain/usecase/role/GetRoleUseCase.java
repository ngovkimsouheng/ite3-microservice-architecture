package kh.edu.istad.iam.domain.usecase.role;

import kh.edu.istad.iam.domain.dto.RoleResult;
import kh.edu.istad.iam.domain.mapper.DomainDtoMapper;
import kh.edu.istad.iam.domain.port.out.RoleRepository;
import kh.edu.istad.iam.domain.core.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetRoleUseCase {

    private final RoleRepository roleRepository;
    private final DomainDtoMapper domainDtoMapper;

    public RoleResult execute(UUID roleUuid) {
        Role role = roleRepository.findByUuid(roleUuid)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleUuid));

        return domainDtoMapper.toRoleResult(role);
    }
}
