package kh.edu.istad.platform.customer.persistence.document;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customerSegments")
public class CustomerSegmentDocument {
    @Id
    private String id;
    private String name;
    private String description;
    private CustomerSegmentType segmentType;
    private Instant createdAt;
    private Instant modifiedAt;
}