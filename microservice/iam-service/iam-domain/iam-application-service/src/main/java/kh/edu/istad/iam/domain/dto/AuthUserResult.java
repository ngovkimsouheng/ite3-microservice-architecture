package kh.edu.istad.iam.domain.dto;

import java.util.Set;
import java.util.UUID;

public record AuthUserResult(
            UUID uuid,
            String username,
            String email,
            String passwordHash,
            boolean enabled,
            boolean accountNonExpired,
            boolean accountNonLocked,
            boolean credentialsNonExpired,
            Set<String> authorities
    ) {}
