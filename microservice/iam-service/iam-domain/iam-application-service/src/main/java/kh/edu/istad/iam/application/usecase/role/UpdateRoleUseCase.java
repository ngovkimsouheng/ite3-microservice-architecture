package kh.edu.istad.iam.application.usecase.role;

import kh.edu.istad.iam.application.dto.CommonDtos.RoleResult;
import kh.edu.istad.iam.application.dto.CommonDtos.UpdateRoleCommand;

public interface UpdateRoleUseCase {
    RoleResult execute(UpdateRoleCommand command);
}
