package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.UserResult;
import java.util.UUID;

public interface GetUserUseCase {
    UserResult execute(UUID userUuid);
}
