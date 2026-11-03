package kh.edu.istad.platform.order.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
public class OrderEntity implements Persistable<UUID> {

    @Id
    @Column("id")
    private UUID id;

    @Column("customer_id")
    private UUID customerId;

    @Column("business_id")
    private UUID businessId;

    @Column("order_address_id")
    private UUID orderAddressId;

    @Column("price")
    private BigDecimal price;

    @Column("tracking_id")
    private UUID trackingId;

    @Column("order_status")
    private String orderStatus;

    @Column("failure_message")
    private String failureMessage;

    // Transient flag to tell Spring Data R2DBC whether to INSERT or UPDATE
    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    public void markNotNew() {
        this.isNew = false;
    }
}

//package kh.edu.istad.platform.order.persistence.entity;
//
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.relational.core.mapping.Column;
//import org.springframework.data.relational.core.mapping.Table;
//
//import java.math.BigDecimal;
//import java.time.ZonedDateTime;
//import java.util.UUID;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@Table("orders")
//public class OrderEntity {
//    @Id
//    private UUID id;
//
//    @Column("customer_id")
//    private UUID customerId;
//
//    @Column("business_id")
//    private UUID businessId;
//
//    private String failureMessage;
//    private UUID orderAddressId;
//
//    @Column("price")
//    private BigDecimal price;
//
//    @Column("tracking_id")
//    private UUID trackingId;
//
//    @Column("order_status")
//    private String orderStatus;
//
//}