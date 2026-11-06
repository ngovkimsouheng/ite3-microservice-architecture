package kh.edu.istad.iam.application.usecase.identity;

import kh.edu.istad.iam.application.dto.CommonDtos.IdentityResult;
import java.util.List;
import java.util.UUID;

public interface ListUserIdentitiesUseCase {
    List<IdentityResult> execute(UUID userUuid);
}
