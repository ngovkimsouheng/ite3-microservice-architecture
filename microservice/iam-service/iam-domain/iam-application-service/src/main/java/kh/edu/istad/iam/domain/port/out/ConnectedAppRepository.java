package kh.edu.istad.iam.domain.port.out;

import kh.edu.istad.iam.domain.dto.ConnectedAppResult;

import java.util.List;

public interface ConnectedAppRepository {
    List<ConnectedAppResult> findAuthorizedForPrincipal(String principalName);

    void revokeAuthorization(String principalName, String clientId);
}
