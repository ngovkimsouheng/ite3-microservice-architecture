package kh.edu.istad.iam.application.usecase.role;

import kh.edu.istad.iam.application.dto.CommonDtos.PageResult;
import kh.edu.istad.iam.application.dto.CommonDtos.RoleSummary;

public interface ListRolesUseCase {
    PageResult<RoleSummary> execute(int page, int size, String sort);
}
