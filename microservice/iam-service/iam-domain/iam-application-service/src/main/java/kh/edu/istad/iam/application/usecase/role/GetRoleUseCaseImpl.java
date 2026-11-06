package kh.edu.istad.iam.application.usecase.role;

import kh.edu.istad.iam.application.dto.CommonDtos.RoleResult;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.domain.core.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetRoleUseCaseImpl implements GetRoleUseCase {

    private final RoleRepository roleRepository;

    @Override
    public RoleResult execute(UUID roleUuid) {
        Role role = roleRepository.findByUuid(roleUuid)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleUuid));

        return new RoleResult(
                role.getUuid(),
                role.getName(),
                role.getDescription(),
                role.isSystem(),
                role.isActive()
        );
    }
}
