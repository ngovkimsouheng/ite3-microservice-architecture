package kh.edu.istad.iam.application.usecase.user;

import kh.edu.istad.iam.application.dto.CommonDtos.UserAuthoritiesResult;
import java.util.UUID;

public interface GetUserAuthoritiesUseCase {
    UserAuthoritiesResult execute(UUID userUuid);
}
