package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.GrantPermissionsToUserCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.PermissionGrantResult;
import java.util.List;

public interface GrantPermissionsToUserUseCase {
    List<PermissionGrantResult> execute(GrantPermissionsToUserCommand command);
}
