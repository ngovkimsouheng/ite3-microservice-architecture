package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.PageResult;
import kh.edu.istad.iam.application.dto.CommonDtos.UserSummary;

public interface ListUsersUseCase {
    PageResult<UserSummary> execute(int page, int size, String sort);
}
