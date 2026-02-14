package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.dto.OrderItemResponse;
import com.cafe.digital_cafe.dto.OrderResponse;
import com.cafe.digital_cafe.dto.UpdateOrderStatusRequest;
import com.cafe.digital_cafe.entity.*;
import com.cafe.digital_cafe.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ChefOrderService {

    private final CafeOrderRepository orderRepository;
    private final CafeRepository cafeRepository;
    private final RestaurantTableRepository tableRepository;
    private final UserRepository userRepository;

    public ChefOrderService(CafeOrderRepository orderRepository, CafeRepository cafeRepository,
                            RestaurantTableRepository tableRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cafeRepository = cafeRepository;
        this.tableRepository = tableRepository;
        this.userRepository = userRepository;
    }

    public List<OrderResponse> getOrders() {
        Long cafeId = getChefCafeId();
        List<OrderStatus> statuses = Arrays.asList(OrderStatus.PLACED, OrderStatus.PREPARING);
        return orderRepository.findByCafeIdAndStatusInOrderByCreatedAtAsc(cafeId, statuses).stream()
                .map(o -> toOrderResponse(o))
                .toList();
    }

    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Long cafeId = getChefCafeId();
        if (request.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }
        if (request.getStatus() != OrderStatus.PREPARING && request.getStatus() != OrderStatus.READY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chef can only set status to preparing or ready");
        }
        CafeOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        if (!order.getCafeId().equals(cafeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Order does not belong to your cafe");
        }
        if (request.getStatus() == OrderStatus.PREPARING && order.getStatus() != OrderStatus.PLACED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only placed orders can be marked as preparing");
        }
        if (request.getStatus() == OrderStatus.READY && order.getStatus() != OrderStatus.PREPARING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only preparing orders can be marked as ready");
        }
        order.setStatus(request.getStatus());
        order = orderRepository.save(order);
        return toOrderResponse(order);
    }

    private Long getChefCafeId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof String email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (user.getRoleType() != RoleType.CHEF || user.getCafeId() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chef access required");
        }
        return user.getCafeId();
    }

    private OrderResponse toOrderResponse(CafeOrder o) {
        String userName = userRepository.findById(o.getUserId()).map(User::getName).orElse(null);
        String cafeName = cafeRepository.findById(o.getCafeId()).map(Cafe::getName).orElse(null);
        String tableNum = tableRepository.findById(o.getTableId()).map(RestaurantTable::getTableNumber).orElse(null);
        OrderResponse r = new OrderResponse();
        r.setId(o.getId());
        r.setUserId(o.getUserId());
        r.setUserName(userName);
        r.setCafeId(o.getCafeId());
        r.setCafeName(cafeName);
        r.setTableId(o.getTableId());
        r.setTableNumber(tableNum);
        r.setBookingId(o.getBookingId());
        r.setOrderDate(o.getOrderDate());
        r.setOrderTime(o.getOrderTime());
        r.setStatus(o.getStatus());
        r.setTotalAmount(o.getTotalAmount());
        r.setCreatedAt(o.getCreatedAt());
        List<OrderItemResponse> items = new ArrayList<>();
        for (OrderItem oi : o.getItems()) {
            OrderItemResponse ir = new OrderItemResponse();
            ir.setId(oi.getId());
            ir.setMenuItemId(oi.getMenuItemId());
            ir.setItemName(oi.getItemName());
            ir.setQuantity(oi.getQuantity());
            ir.setUnitPrice(oi.getUnitPrice());
            ir.setSubtotal(oi.getSubtotal());
            items.add(ir);
        }
        r.setItems(items);
        return r;
    }
}
