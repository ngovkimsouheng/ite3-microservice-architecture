package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.AssignRolesToUserCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.RoleAssignmentResult;
import java.util.List;

public interface AssignRolesToUserUseCase {
    List<RoleAssignmentResult> execute(AssignRolesToUserCommand command);
}
