package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.AuthUserResult;

public interface LoadUserForAuthenticationUseCase {
    AuthUserResult execute(String usernameOrEmail);
}
