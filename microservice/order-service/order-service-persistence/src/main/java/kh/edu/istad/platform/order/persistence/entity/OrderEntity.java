package kh.edu.istad.platform.order.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Table("orders")
public class OrderEntity {
    @Id
    private UUID id;

    @Column("customer_id")
    private UUID customerId;

    @Column("business_id")
    private UUID businessId;

    private String failureMessage;
    private UUID orderAddressId;

    @Column("price")
    private BigDecimal price;

    @Column("tracking_id")
    private UUID trackingId;

    @Column("order_status")
    private String orderStatus;

}