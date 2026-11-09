package kh.edu.istad.iam.domain.dto;

import java.util.UUID;

public record ResetUserPasswordCommand(
            UUID userUuid,
            String newPassword
    ) {}
