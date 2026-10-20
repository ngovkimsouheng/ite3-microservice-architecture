package kh.edu.istad.platform.customer.persistence.document;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegmentType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customerSegments")
public class CustomerSegmentDocument implements Persistable<String> {

    @Id
    private String id;
    private String name;
    private String description;
    private CustomerSegmentType segmentType;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant modifiedAt;

    @Transient
    @Builder.Default
    private boolean isNew = false;

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    public CustomerSegmentDocument asNew() {
        this.isNew = true;
        return this;
    }
}