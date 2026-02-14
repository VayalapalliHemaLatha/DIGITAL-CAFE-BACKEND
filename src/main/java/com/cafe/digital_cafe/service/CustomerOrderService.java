package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.dto.*;
import com.cafe.digital_cafe.entity.*;
import com.cafe.digital_cafe.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerOrderService {

    private final CafeOrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final CafeRepository cafeRepository;
    private final RestaurantTableRepository tableRepository;
    private final UserRepository userRepository;

    public CustomerOrderService(CafeOrderRepository orderRepository, MenuItemRepository menuItemRepository,
                                CafeRepository cafeRepository, RestaurantTableRepository tableRepository,
                                UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.cafeRepository = cafeRepository;
        this.tableRepository = tableRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow();
        Cafe cafe = cafeRepository.findById(request.getCafeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cafe not found"));
        RestaurantTable table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Table not found"));
        if (!table.getCafeId().equals(request.getCafeId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Table does not belong to this cafe");
        }
        CafeOrder order = new CafeOrder();
        order.setUserId(userId);
        order.setCafeId(request.getCafeId());
        order.setTableId(request.getTableId());
        order.setBookingId(request.getBookingId());
        order.setOrderDate(request.getOrderDate());
        order.setOrderTime(request.getOrderTime());
        order.setStatus(OrderStatus.PLACED);
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest ir : request.getItems()) {
            MenuItem mi = menuItemRepository.findById(ir.getMenuItemId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found: " + ir.getMenuItemId()));
            if (!mi.getCafeId().equals(request.getCafeId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu item does not belong to this cafe");
            }
            if (!mi.isAvailable()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu item not available: " + mi.getName());
            }
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setMenuItemId(mi.getId());
            oi.setItemName(mi.getName());
            oi.setQuantity(ir.getQuantity());
            oi.setUnitPrice(mi.getPrice());
            order.getItems().add(oi);
            total = total.add(mi.getPrice().multiply(BigDecimal.valueOf(ir.getQuantity())));
        }
        order.setTotalAmount(total);
        order = orderRepository.save(order);
        return toOrderResponse(order, user.getName(), cafe.getName(), table.getTableNumber());
    }

    public List<OrderResponse> getMyOrders() {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId).orElse(null);
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(o -> {
                    String cafeName = cafeRepository.findById(o.getCafeId()).map(Cafe::getName).orElse(null);
                    String tableNum = tableRepository.findById(o.getTableId()).map(RestaurantTable::getTableNumber).orElse(null);
                    return toOrderResponse(o, user != null ? user.getName() : null, cafeName, tableNum);
                })
                .collect(Collectors.toList());
    }

    public OrderResponse getOrder(Long id) {
        Long userId = getCurrentUserId();
        CafeOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        if (!order.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your order");
        }
        User user = userRepository.findById(userId).orElse(null);
        String cafeName = cafeRepository.findById(order.getCafeId()).map(Cafe::getName).orElse(null);
        String tableNum = tableRepository.findById(order.getTableId()).map(RestaurantTable::getTableNumber).orElse(null);
        return toOrderResponse(order, user != null ? user.getName() : null, cafeName, tableNum);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof String email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"))
                .getId();
    }

    private OrderResponse toOrderResponse(CafeOrder o, String userName, String cafeName, String tableNumber) {
        OrderResponse r = new OrderResponse();
        r.setId(o.getId());
        r.setUserId(o.getUserId());
        r.setUserName(userName);
        r.setCafeId(o.getCafeId());
        r.setCafeName(cafeName);
        r.setTableId(o.getTableId());
        r.setTableNumber(tableNumber);
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
