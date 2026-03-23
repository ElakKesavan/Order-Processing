package com.peerislands.orders.mapper;

import com.peerislands.orders.model.Order;
import com.peerislands.orders.model.OrderItem;
import com.peerislands.orders.payload.response.OrderItemResponse;
import com.peerislands.orders.payload.response.OrderResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toOrderResponse(Order order);

    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}
