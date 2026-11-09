package kh.edu.istad.iam.restapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
            @NotBlank(message = "Username cannot be blank")
            @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
            String username,

            @NotBlank(message = "Email cannot be blank")
            @Email(message = "must be a well-formed email address")
            String email,

            @NotBlank(message = "Password cannot be blank")
            String password,

            @NotBlank(message = "Password confirmation cannot be blank")
            String confirmPassword,

            @NotBlank(message = "Given name cannot be blank")
            String givenName,

            @NotBlank(message = "Family name cannot be blank")
            String familyName,

            @NotBlank(message = "Locale cannot be blank")
            String locale
    ) {}
