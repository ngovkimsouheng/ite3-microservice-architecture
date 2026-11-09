package kh.edu.istad.iam.restapi.dto;

import java.util.List;

public record PageResponse<T>(
            List<T> content,
            PageMetadata page
    ) {}
