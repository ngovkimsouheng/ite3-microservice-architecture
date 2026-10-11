package kh.edu.istad.platform.order.restapi.mapper;

import kh.edu.istad.platform.order.domain.dto.CreateOrderCommand;
import kh.edu.istad.platform.order.domain.dto.CreateOrderResult;
import kh.edu.istad.platform.order.domain.dto.OrderItemCommand;
import kh.edu.istad.platform.order.restapi.dto.OrderCreateRequest;
import kh.edu.istad.platform.order.restapi.dto.OrderCreateResponse;
import kh.edu.istad.platform.order.restapi.dto.OrderItemRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderWebMapper {

    CreateOrderCommand toCommand(OrderCreateRequest request);

    OrderItemCommand toCommand(OrderItemRequest request);

    OrderCreateResponse toResponse(CreateOrderResult result);
}