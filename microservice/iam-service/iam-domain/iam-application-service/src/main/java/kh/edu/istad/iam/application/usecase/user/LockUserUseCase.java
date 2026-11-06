package kh.edu.istad.iam.application.usecase.user;

import java.util.UUID;

public interface LockUserUseCase {
    void execute(UUID userUuid);
}
