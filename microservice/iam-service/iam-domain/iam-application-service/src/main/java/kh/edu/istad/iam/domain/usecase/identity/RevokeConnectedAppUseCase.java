package kh.edu.istad.iam.domain.usecase.identity;

import kh.edu.istad.iam.domain.port.out.ConnectedAppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RevokeConnectedAppUseCase {

    private final ConnectedAppRepository connectedAppRepository;

    @Transactional
    public void execute(String principalName, String clientId) {
        if (principalName == null || principalName.isBlank() || clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("An authenticated user and OAuth client are required.");
        }
        connectedAppRepository.revokeAuthorization(principalName, clientId);
    }
}
