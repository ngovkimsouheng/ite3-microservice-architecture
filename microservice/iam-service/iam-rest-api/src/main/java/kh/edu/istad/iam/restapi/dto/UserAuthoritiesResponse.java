package kh.edu.istad.iam.restapi.dto;

import java.util.List;
import java.util.Set;

public record UserAuthoritiesResponse(
        Set<String> roles,
        Set<String> effectivePermissions,
        List<RoleDto> roleDetails
) {}
