package kh.edu.istad.iam.application.usecase.permission;

import kh.edu.istad.iam.application.dto.CommonDtos.PageResult;
import kh.edu.istad.iam.application.dto.CommonDtos.PermissionResult;

public interface ListPermissionsUseCase {
    PageResult<PermissionResult> execute(int page, int size, String sort);
}
