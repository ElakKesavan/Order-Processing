package com.peerislands.orders.mapper;

import com.peerislands.orders.model.Order;
import com.peerislands.orders.model.OrderItem;
import com.peerislands.orders.payload.response.OrderItemResponse;
import com.peerislands.orders.payload.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "userEmail")
    OrderResponse toOrderResponse(Order order);

    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}
