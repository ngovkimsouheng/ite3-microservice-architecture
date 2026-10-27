
package kh.edu.istad.platform.order.persistence.entity;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Table("order_items")
public class OrderItemEntity {

    @Id
    @Column("order_item_id")
    private Long orderItemId;

    @Column("order_id")
    private UUID orderId;

    @Column("product_id")
    private UUID productId;

    @Column("quantity")
    private Integer quantity;

    @Column("price")
    private BigDecimal price;

    @Column("sub_total")
    private BigDecimal subTotal;
}
