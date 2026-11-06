package kh.edu.istad.iam.application.usecase.permission;

import java.util.UUID;

public interface DeletePermissionUseCase {
    void execute(UUID permissionUuid);
}
