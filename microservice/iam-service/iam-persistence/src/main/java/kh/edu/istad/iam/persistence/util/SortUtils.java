package kh.edu.istad.iam.persistence.util;

import org.springframework.data.domain.Sort;
import java.util.Set;

public final class SortUtils {

    private SortUtils() {}

    public static Sort parseSort(String sort, String defaultProperty) {
        return parseSort(sort, defaultProperty, Sort.Direction.ASC, null);
    }

    public static Sort parseSort(String sort, String defaultProperty, Sort.Direction defaultDirection) {
        return parseSort(sort, defaultProperty, defaultDirection, null);
    }

    public static Sort parseSort(String sort, String defaultProperty, Sort.Direction defaultDirection, Set<String> allowedProperties) {
        Sort.Direction fallbackDir = defaultDirection != null ? defaultDirection : Sort.Direction.ASC;
        if (sort == null || sort.isBlank()) {
            return Sort.by(fallbackDir, defaultProperty);
        }

        try {
            String[] parts = sort.split(",");
            if (parts.length == 0 || parts[0].isBlank()) {
                return Sort.by(fallbackDir, defaultProperty);
            }

            String property = parts[0].trim();
            if (allowedProperties != null && !allowedProperties.contains(property)) {
                return Sort.by(fallbackDir, defaultProperty);
            }

            Sort.Direction direction = fallbackDir;
            if (parts.length > 1) {
                String dirStr = parts[1].trim();
                if ("desc".equalsIgnoreCase(dirStr)) {
                    direction = Sort.Direction.DESC;
                } else if ("asc".equalsIgnoreCase(dirStr)) {
                    direction = Sort.Direction.ASC;
                }
            }

            return Sort.by(direction, property);
        } catch (Exception e) {
            return Sort.by(fallbackDir, defaultProperty);
        }
    }
}
