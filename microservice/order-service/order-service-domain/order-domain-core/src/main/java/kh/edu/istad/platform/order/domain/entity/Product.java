package kh.edu.istad.platform.order.domain.entity;

import java.util.UUID;

public record Product (
        UUID productId,
        String name
){
}