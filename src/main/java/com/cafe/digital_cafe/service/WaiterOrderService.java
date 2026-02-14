package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.dto.OrderItemResponse;
import com.cafe.digital_cafe.dto.OrderResponse;
import com.cafe.digital_cafe.dto.UpdateOrderStatusRequest;
import com.cafe.digital_cafe.dto.UpdateOrderStatusRequest;
import com.cafe.digital_cafe.entity.*;
import com.cafe.digital_cafe.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class WaiterOrderService {

    private final CafeOrderRepository orderRepository;
    private final CafeRepository cafeRepository;
    private final RestaurantTableRepository tableRepository;
    private final UserRepository userRepository;

    public WaiterOrderService(CafeOrderRepository orderRepository, CafeRepository cafeRepository,
                              RestaurantTableRepository tableRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cafeRepository = cafeRepository;
        this.tableRepository = tableRepository;
        this.userRepository = userRepository;
    }

    /** Orders ready to serve - waiter sees which table to serve */
    public List<OrderResponse> getReadyOrders() {
        Long cafeId = getWaiterCafeId();
        return orderRepository.findByCafeIdAndStatusOrderByCreatedAtAsc(cafeId, OrderStatus.READY).stream()
                .map(this::toOrderResponse)
                .toList();
    }

    /** All orders for waiter view (optional - to see full flow) */
    public List<OrderResponse> getAllOrders() {
        Long cafeId = getWaiterCafeId();
        return orderRepository.findByCafeIdOrderByCreatedAtDesc(cafeId).stream()
                .map(this::toOrderResponse)
                .toList();
    }

    public OrderResponse markAsServed(Long orderId, UpdateOrderStatusRequest request) {
        Long cafeId = getWaiterCafeId();
        CafeOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        if (!order.getCafeId().equals(cafeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Order does not belong to your cafe");
        }
        if (order.getStatus() != OrderStatus.READY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only ready orders can be marked as served");
        }
        order.setStatus(OrderStatus.SERVED);
        order = orderRepository.save(order);
        return toOrderResponse(order);
    }

    private Long getWaiterCafeId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof String email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (user.getRoleType() != RoleType.WAITER || user.getCafeId() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Waiter access required");
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
