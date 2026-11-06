package kh.edu.istad.iam.application.port.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserCommand(
        @NotBlank @Size(max = 50) String username,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String confirmPassword,
        @NotBlank @Size(max = 100) String givenName,
        @NotBlank @Size(max = 100) String familyName,
        @NotBlank @Size(max = 10) String locale
) {}