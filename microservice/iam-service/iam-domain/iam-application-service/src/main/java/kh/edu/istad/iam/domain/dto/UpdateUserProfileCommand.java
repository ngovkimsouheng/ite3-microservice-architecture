package kh.edu.istad.iam.domain.dto;

import java.util.UUID;

public record UpdateUserProfileCommand(
            UUID userUuid,
            String username,
            String givenName,
            String nickname,
            String phoneNumber,
            String locale
    ) {}
