package kh.edu.istad.iam.application.usecase.role;

import kh.edu.istad.iam.application.dto.CommonDtos.AssignPermissionsToRoleCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.PermissionAssignmentResult;
import java.util.List;

public interface AssignPermissionsToRoleUseCase {
    List<PermissionAssignmentResult> execute(AssignPermissionsToRoleCommand command);
}
