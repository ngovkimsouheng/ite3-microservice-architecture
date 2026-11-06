package kh.edu.istad.iam.application.usecase.permission;

import java.util.UUID;

public interface DeactivatePermissionUseCase {
    void execute(UUID permissionUuid);
}
