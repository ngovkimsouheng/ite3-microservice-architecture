package kh.edu.istad.platform.customer.persistence.document;


import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "customerDocument")
public class CustomerDocument {

    @Id
    private UUID id;

    private String username;
    private String familyName;
    private String givenName;
    private String email;
    private String phoneNumber;
    private CustomerStatus status;

    @CreatedBy
    private Instant createdBy;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedBy
    private String modifiedBy;

    @LastModifiedDate
    private String modifiedAt;
}