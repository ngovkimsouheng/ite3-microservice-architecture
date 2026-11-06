package kh.edu.istad.iam.application.usecase.permission;

import kh.edu.istad.iam.application.dto.CommonDtos.PermissionResult;
import java.util.UUID;

public interface GetPermissionUseCase {
    PermissionResult execute(UUID permissionUuid);
}
