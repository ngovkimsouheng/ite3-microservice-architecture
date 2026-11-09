package kh.edu.istad.iam.domain.dto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record UserAuthoritiesResult(
            UUID userUuid,
            Set<String> roles,
            Set<String> permissions,
            List<UserRoleResult> roleDetails
    ) {}
