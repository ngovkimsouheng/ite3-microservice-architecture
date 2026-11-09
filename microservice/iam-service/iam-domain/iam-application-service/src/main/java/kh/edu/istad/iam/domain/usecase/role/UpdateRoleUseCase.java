package kh.edu.istad.iam.domain.usecase.role;

import kh.edu.istad.iam.domain.dto.RoleResult;
import kh.edu.istad.iam.domain.dto.UpdateRoleCommand;
import kh.edu.istad.iam.domain.mapper.DomainDtoMapper;
import kh.edu.istad.iam.domain.port.out.RoleRepository;
import kh.edu.istad.iam.domain.core.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateRoleUseCase {

    private final RoleRepository roleRepository;
    private final DomainDtoMapper domainDtoMapper;

    @Transactional
    public RoleResult execute(UpdateRoleCommand command) {
        Role role = roleRepository.findByUuid(command.roleUuid())
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + command.roleUuid()));

        if (role.isSystem()) {
            throw new IllegalStateException("System role cannot be modified");
        }

        if (!role.getName().equalsIgnoreCase(command.name()) && roleRepository.existsByName(command.name())) {
            throw new IllegalArgumentException("Role name already exists: " + command.name());
        }

        role.setName(command.name());
        role.setDescription(command.description());

        Role updated = roleRepository.save(role);

        return domainDtoMapper.toRoleResult(updated);
    }
}
