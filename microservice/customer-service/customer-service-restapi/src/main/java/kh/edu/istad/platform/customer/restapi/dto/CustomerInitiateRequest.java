package kh.edu.istad.platform.customer.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;

public record CustomerInitiateRequest(
        @NotBlank
        String username,
        @NotBlank
        String familyName,
        @NotBlank
        String givenName,
        @NotBlank
        String email,
        @NotBlank
        String phoneNumber


) {
}
