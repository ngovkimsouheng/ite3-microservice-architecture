package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.ChangePasswordCommand;
import java.util.UUID;

public interface ChangePasswordUseCase {
    void execute(UUID userUuid, ChangePasswordCommand command);
}
