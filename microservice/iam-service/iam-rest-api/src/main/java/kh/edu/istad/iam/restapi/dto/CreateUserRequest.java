package kh.edu.istad.iam.restapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateUserRequest(
            @NotBlank String username,
            @NotBlank @Email String email,
            @NotBlank String password,
            @NotBlank String givenName,
            @NotBlank String familyName,
            String gender,
            LocalDate birthdate,
            String phoneNumber,
            String zoneinfo,
            @NotBlank String locale,
            AddressDto address,
            List<UUID> roleUuids
    ) {}
