package kh.edu.istad.iam.application.usecase.role;

import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.domain.core.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivateRoleUseCaseImpl implements ActivateRoleUseCase {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void execute(UUID roleUuid) {
        Role role = roleRepository.findByUuid(roleUuid)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleUuid));

        role.setActive(true);
        roleRepository.save(role);
    }
}