package kh.edu.istad.iam.application.usecase.identity;

import java.util.UUID;

public interface UnlinkIdentityUseCase {
    void execute(UUID userUuid, UUID identityUuid);
}
