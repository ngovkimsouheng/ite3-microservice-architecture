package kh.edu.istad.iam.application.usecase.role;

import kh.edu.istad.iam.application.dto.CommonDtos.CreateRoleCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.RoleResult;

public interface CreateRoleUseCase {
    RoleResult execute(CreateRoleCommand command);
}
