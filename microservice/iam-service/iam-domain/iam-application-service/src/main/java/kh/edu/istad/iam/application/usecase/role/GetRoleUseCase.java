package kh.edu.istad.iam.application.usecase.role;

import kh.edu.istad.iam.application.dto.CommonDtos.RoleResult;
import java.util.UUID;

public interface GetRoleUseCase {
    RoleResult execute(UUID roleUuid);
}
