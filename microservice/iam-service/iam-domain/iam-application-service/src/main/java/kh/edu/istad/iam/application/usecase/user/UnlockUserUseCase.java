package kh.edu.istad.iam.application.usecase.user;

import java.util.UUID;

public interface UnlockUserUseCase {
    void execute(UUID userUuid);
}
