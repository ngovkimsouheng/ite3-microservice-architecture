package kh.edu.istad.iam.application.usecase.permission;

import kh.edu.istad.iam.application.dto.CommonDtos.CreatePermissionCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.PermissionResult;

public interface CreatePermissionUseCase {
    PermissionResult execute(CreatePermissionCommand command);
}
