package kh.edu.istad.iam.application.usecase.user;

import java.util.UUID;

public interface DeleteUserUseCase {
    void execute(UUID userUuid);
}
