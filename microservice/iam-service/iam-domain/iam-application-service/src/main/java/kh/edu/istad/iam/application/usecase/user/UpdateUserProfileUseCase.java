package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.UpdateUserProfileCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.UserResult;

public interface UpdateUserProfileUseCase {
    UserResult execute(UpdateUserProfileCommand command);
}
