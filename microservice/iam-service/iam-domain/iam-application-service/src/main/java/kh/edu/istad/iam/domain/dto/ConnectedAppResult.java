package kh.edu.istad.iam.domain.dto;

import java.util.Set;

public record ConnectedAppResult(String clientId, String clientName, Set<String> scopes) {
}
