package kh.edu.istad.iam.application.usecase.user;

import java.util.UUID;

public interface RevokeRoleFromUserUseCase {
    void execute(UUID userUuid, UUID roleUuid, String reason);
}
