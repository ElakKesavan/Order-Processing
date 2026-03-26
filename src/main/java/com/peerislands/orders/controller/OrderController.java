package com.peerislands.orders.controller;

import com.peerislands.orders.mapper.OrderMapper;
import com.peerislands.orders.model.Order;
import com.peerislands.orders.model.User;
import com.peerislands.orders.payload.request.OrderFilter;
import com.peerislands.orders.payload.request.OrderRequest;
import com.peerislands.orders.payload.request.UpdateOrderStatusRequest;
import com.peerislands.orders.payload.response.MessageResponse;
import com.peerislands.orders.payload.response.OrderResponse;
import com.peerislands.orders.security.services.AuthenticationHelper;
import com.peerislands.orders.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order lifecycle management endpoints")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private final OrderMapper orderMapper;

    private final AuthenticationHelper authenticationHelper;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(
            summary = "Create a new order",
            description =
                    "Places a new order with the given items. Only accessible by CUSTOMER role.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Order created successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = OrderResponse.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Validation or business rule error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProblemDetail.class))),
                @ApiResponse(
                        responseCode = "401",
                        description = "Missing or invalid JWT token",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProblemDetail.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "User does not have CUSTOMER role",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProblemDetail.class)))
            })
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest orderRequest) {
        User user = authenticationHelper.getCurrentUser();
        Order createdOrder = orderService.createOrder(user, orderRequest);
        return ResponseEntity.ok(orderMapper.toOrderResponse(createdOrder));
    }

    @GetMapping
    @Operation(
            summary = "List orders (paginated)",
            description =
                    "Returns paginated orders. ADMINs see all orders; CUSTOMERs see only their"
                            + " own. Optionally filter by status or sort.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Page of orders returned successfully"),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProblemDetail.class)))
            })
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @ParameterObject @ModelAttribute OrderFilter filters,
            @Parameter(
                            description = "Sort by field (orderId, userId, updatedAt, createdAt)",
                            example = "updatedAt")
                    @RequestParam(required = false)
                    String sortBy,
            @Parameter(description = "Zero-based page index", example = "0")
                    @RequestParam(defaultValue = "0")
                    int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20")
                    int size) {

        User user = authenticationHelper.getCurrentUser();

        Page<Order> orders = orderService.getOrders(user, filters, page, size, sortBy);
        return ResponseEntity.ok(orders.map(orderMapper::toOrderResponse));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get order by ID",
            description =
                    "Retrieves a single order. CUSTOMERs can only view their own orders; ADMINs"
                            + " can view any.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Order found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = OrderResponse.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "Not authorized to view this order",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProblemDetail.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Order not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProblemDetail.class)))
            })
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "Order ID", example = "101") @PathVariable Long id) {
        User user = authenticationHelper.getCurrentUser();
        Order order = orderService.getOrderById(id, user);
        return ResponseEntity.ok(orderMapper.toOrderResponse(order));
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update order status",
            description =
                    "Transitions an order to a new status. Role-based rules apply for allowed"
                            + " transitions.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Status updated successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = MessageResponse.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request (e.g. invalid enum value)",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProblemDetail.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "Not authorized for this status transition",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProblemDetail.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Order not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProblemDetail.class))),
                @ApiResponse(
                        responseCode = "409",
                        description = "Invalid state transition",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ProblemDetail.class)))
            })
    public ResponseEntity<MessageResponse> updateOrderStatus(
            @Parameter(description = "Order ID", example = "101") @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        User user = authenticationHelper.getCurrentUser();
        orderService.updateOrderStatus(id, request.getStatus(), user);
        return ResponseEntity.ok(
                new MessageResponse("Order status updated to " + request.getStatus()));
    }
}
