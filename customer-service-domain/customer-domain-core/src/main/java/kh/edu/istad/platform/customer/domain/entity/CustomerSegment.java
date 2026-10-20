package kh.edu.istad.platform.customer.domain.entity;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentType;
import lombok.*;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSegment {
    private String id;
    private String name;
    private String description;
    private CustomerSegmentType segmentType;
    private Instant createdAt;
    private Instant modifiedAt;
}