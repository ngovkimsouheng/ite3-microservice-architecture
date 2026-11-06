package kh.edu.istad.iam.application.usecase.permission;

import kh.edu.istad.iam.application.dto.CommonDtos.PermissionResult;
import kh.edu.istad.iam.application.dto.CommonDtos.UpdatePermissionCommand;

public interface UpdatePermissionUseCase {
    PermissionResult execute(UpdatePermissionCommand command);
}
