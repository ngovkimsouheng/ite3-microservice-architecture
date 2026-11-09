package kh.edu.istad.iam.domain.usecase.identity;

import kh.edu.istad.iam.domain.dto.FederatedIdentityCommand;
import kh.edu.istad.iam.domain.dto.IdentityResult;
import java.util.UUID;

public interface LinkIdentityUseCase {
    IdentityResult execute(UUID userUuid, FederatedIdentityCommand command);
}
