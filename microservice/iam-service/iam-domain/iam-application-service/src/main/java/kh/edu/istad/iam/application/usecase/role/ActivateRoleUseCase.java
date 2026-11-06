package kh.edu.istad.iam.application.usecase.role;

import java.util.UUID;

public interface ActivateRoleUseCase {
    void execute(UUID roleUuid);
}
