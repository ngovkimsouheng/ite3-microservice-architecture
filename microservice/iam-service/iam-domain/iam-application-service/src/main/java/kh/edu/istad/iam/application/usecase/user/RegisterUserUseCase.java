package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.RegisterUserCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.UserResult;

public interface RegisterUserUseCase {
    UserResult execute(RegisterUserCommand command);
}
