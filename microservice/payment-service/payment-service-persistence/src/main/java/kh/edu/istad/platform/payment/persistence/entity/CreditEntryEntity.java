package kh.edu.istad.platform.payment.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "credit_entries")
@Entity
public class CreditEntryEntity {
    @Id
    private UUID id;
    private UUID customerId;
    private BigDecimal totalCreditAmount;
}
