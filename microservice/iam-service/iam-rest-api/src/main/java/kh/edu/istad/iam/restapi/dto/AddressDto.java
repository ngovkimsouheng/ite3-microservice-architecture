package kh.edu.istad.iam.restapi.dto;

public record AddressDto(
            String streetAddress,
            String locality,
            String region,
            String postalCode,
            String country
    ) {}
