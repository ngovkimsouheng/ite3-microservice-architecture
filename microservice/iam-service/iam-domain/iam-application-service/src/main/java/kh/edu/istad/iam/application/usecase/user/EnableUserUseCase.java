package kh.edu.istad.iam.application.usecase.user;

import java.util.UUID;

public interface EnableUserUseCase {
    void execute(UUID userUuid);
}
