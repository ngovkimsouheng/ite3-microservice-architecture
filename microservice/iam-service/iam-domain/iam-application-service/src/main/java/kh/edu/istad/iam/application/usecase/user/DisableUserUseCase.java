package kh.edu.istad.iam.application.usecase.user;

import java.util.UUID;

public interface DisableUserUseCase {
    void execute(UUID userUuid);
}
