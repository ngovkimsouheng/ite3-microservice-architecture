package kh.edu.istad.iam.application.usecase.role;

import java.util.UUID;

public interface DeleteRoleUseCase {
    void execute(UUID roleUuid);
}
