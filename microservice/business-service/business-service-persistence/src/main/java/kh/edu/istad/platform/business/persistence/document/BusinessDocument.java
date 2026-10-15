package kh.edu.istad.platform.business.persistence.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "businesses")
public class BusinessDocument {
    @Id
    private UUID id;

    @Indexed(unique = true)
    private String legalName;
    private String displayName;

    private String email;
    private String phoneNumber;

    private boolean active;
    private List<ProductDocument> products;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDocument {
        private UUID id;
        private String name;
        private BigDecimal price;
        private int quantity;
        private boolean available;
    }
}
