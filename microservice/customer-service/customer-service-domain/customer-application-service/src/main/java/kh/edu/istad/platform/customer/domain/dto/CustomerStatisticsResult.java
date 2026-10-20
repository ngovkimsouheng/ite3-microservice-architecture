package kh.edu.istad.platform.customer.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerStatisticsResult {
    private long totalCustomers;
    private Map<String, Long> countByStatus;
    private Map<String, Long> countBySegment;
}