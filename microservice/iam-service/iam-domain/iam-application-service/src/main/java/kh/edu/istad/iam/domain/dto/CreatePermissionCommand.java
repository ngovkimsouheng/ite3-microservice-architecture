package kh.edu.istad.iam.domain.dto;

public record CreatePermissionCommand(String resource, String action, String description) {}
