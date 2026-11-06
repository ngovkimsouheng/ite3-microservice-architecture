package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.ResetUserPasswordCommand;

public interface ResetUserPasswordUseCase {
    void execute(ResetUserPasswordCommand command);
}
