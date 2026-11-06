package kh.edu.istad.iam.application.usecase.role;

import java.util.UUID;

public interface RevokePermissionFromRoleUseCase {
    void execute(UUID roleUuid, UUID permissionUuid, String reason);
}
