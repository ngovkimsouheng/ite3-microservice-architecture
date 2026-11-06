package kh.edu.istad.iam.application.usecase.permission;

import java.util.UUID;

public interface ActivatePermissionUseCase {
    void execute(UUID permissionUuid);
}
