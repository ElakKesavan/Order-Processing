package com.peerislands.orders.order.mapper;

import com.peerislands.orders.order.model.Order;
import com.peerislands.orders.order.model.OrderItem;
import com.peerislands.orders.order.payload.response.OrderItemResponse;
import com.peerislands.orders.order.payload.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "userEmail")
    OrderResponse toOrderResponse(Order order);

    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}
