package kh.edu.istad.iam.restapi.dto;

public record PasswordPolicyResponse(
        int minPasswordLength,
        boolean requireUppercase,
        boolean requireLowercase,
        boolean requireDigits,
        boolean requireSpecialChar,
        int passwordExpireDays
) {}
