package kh.edu.istad.iam.application.usecase.identity;

import kh.edu.istad.iam.application.dto.CommonDtos.AuthUserResult;
import kh.edu.istad.iam.application.dto.CommonDtos.FederatedIdentityCommand;

public interface FindOrProvisionFederatedUserUseCase {
    AuthUserResult execute(FederatedIdentityCommand command);
}
