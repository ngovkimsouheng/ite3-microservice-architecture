package kh.edu.istad.iam.application.usecase.user;

import java.util.UUID;

public interface RevokePermissionFromUserUseCase {
    void execute(UUID userUuid, UUID permissionUuid, String reason);
}
