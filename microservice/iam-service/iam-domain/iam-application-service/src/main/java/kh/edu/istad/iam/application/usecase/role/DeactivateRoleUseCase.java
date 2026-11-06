package kh.edu.istad.iam.application.usecase.role;

import java.util.UUID;

public interface DeactivateRoleUseCase {
    void execute(UUID roleUuid);
}
