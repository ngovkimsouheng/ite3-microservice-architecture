package kh.edu.istad.iam.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileEditForm(
        @NotBlank @Size(max = 100) String givenName,
        @Size(max = 100) String nickname,
        @Size(max = 20) String phoneNumber,
        @NotBlank String locale
) {
}
