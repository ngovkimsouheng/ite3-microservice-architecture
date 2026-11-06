package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.CreateUserCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.UserResult;

public interface CreateUserUseCase {
    UserResult execute(CreateUserCommand command);
}
