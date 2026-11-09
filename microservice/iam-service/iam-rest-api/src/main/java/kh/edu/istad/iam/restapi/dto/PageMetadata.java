package kh.edu.istad.iam.restapi.dto;

public record PageMetadata(
            int number,
            int size,
            long totalElements,
            int totalPages
    ) {}
