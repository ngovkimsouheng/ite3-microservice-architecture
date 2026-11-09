package kh.edu.istad.iam.restapi.dto;

import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(
            @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
            String username,
            String givenName,
            String nickname,
            @Size(max = 20, message = "Phone number must not exceed 20 characters")
            String phoneNumber,
            String locale
    ) {}
