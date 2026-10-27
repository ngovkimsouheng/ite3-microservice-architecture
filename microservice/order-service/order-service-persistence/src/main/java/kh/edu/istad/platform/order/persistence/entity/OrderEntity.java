package kh.edu.istad.platform.order.persistence.entity;

import kh.edu.istad.platform.order.domain.valueobject.OrderStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Table("orders")
@Getter
@Setter
public class OrderEntity implements Persistable<UUID> {

    @Id
    private UUID orderId;
    private UUID customerId;
    private UUID businessId;
    private UUID trackingId;
    private BigDecimal price;
    private OrderStatus orderStatus;
    private String street;
    private String postalCode;
    private String city;

    @Transient
    private boolean isNew = true;

    @Override
    public UUID getId() {
        return this.orderId;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }
}