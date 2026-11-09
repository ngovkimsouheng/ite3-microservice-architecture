package kh.edu.istad.iam.domain.usecase.identity;

import kh.edu.istad.iam.domain.dto.ConnectedAppResult;
import kh.edu.istad.iam.domain.port.out.ConnectedAppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListConnectedAppsUseCase {

    private final ConnectedAppRepository connectedAppRepository;

    public List<ConnectedAppResult> execute(String principalName) {
        if (principalName == null || principalName.isBlank()) {
            throw new IllegalArgumentException("An authenticated user is required.");
        }
        return connectedAppRepository.findAuthorizedForPrincipal(principalName);
    }
}
