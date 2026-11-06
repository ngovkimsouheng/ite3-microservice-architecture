package kh.edu.istad.iam.application.usecase.identity;

import kh.edu.istad.iam.application.dto.CommonDtos.FederatedIdentityCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.IdentityResult;
import java.util.UUID;

public interface LinkIdentityUseCase {
    IdentityResult execute(UUID userUuid, FederatedIdentityCommand command);
}
