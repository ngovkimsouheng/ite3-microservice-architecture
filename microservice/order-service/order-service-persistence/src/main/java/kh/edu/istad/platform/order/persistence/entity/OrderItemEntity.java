package kh.edu.istad.platform.order.persistence.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Table("order_items")
public class OrderItemEntity {
    @Id
    OrderItemEntityId id;

    @Column("product_id")
    private UUID productId;

    @Column("product_name")
    private String productName;

    @Column("quantity")
    private Integer quantity;

    @Column("price")
    private BigDecimal price;

    @Column("sub_total")
    private BigDecimal subTotal;
}