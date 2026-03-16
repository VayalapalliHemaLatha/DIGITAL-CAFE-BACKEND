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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Table does not belong to this cafe. Table " + request.getTableId() + " belongs to cafe " + table.getCafeId() + ". Use a table for cafe " + request.getCafeId() + ".");
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
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Menu item " + ir.getMenuItemId() + " (" + mi.getName() + ") belongs to cafe " + mi.getCafeId() + ", not cafe " + request.getCafeId() + ". Use GET /api/cafes/" + request.getCafeId() + " to get menu and table ids for this cafe.");
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

    /**
     * Cart-style order: cafe is inferred from items (all must be from same cafe).
     * Table is optional; if omitted, first table of that cafe is used.
     */
    @Transactional
    public OrderResponse createOrderFromCart(CreateOrderFromCartRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one item is required");
        }
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow();

        Long cafeId = null;
        for (OrderItemRequest ir : request.getItems()) {
            MenuItem mi = menuItemRepository.findById(ir.getMenuItemId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found: " + ir.getMenuItemId()));
            if (cafeId == null) {
                cafeId = mi.getCafeId();
            } else if (!mi.getCafeId().equals(cafeId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "All items must be from the same cafe. Menu item " + ir.getMenuItemId() + " (\"" + mi.getName() + "\") belongs to cafe " + mi.getCafeId() + "; other items are from cafe " + cafeId + ". Use GET /api/menu?cafeId=" + cafeId + " to add only that cafe's items, or remove item " + ir.getMenuItemId() + " from the cart.");
            }
            if (!mi.isAvailable()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu item not available: " + mi.getName());
            }
        }

        final Long inferredCafeId = cafeId;
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Cafe not found (id " + inferredCafeId + "). The menu item(s) reference a cafe that may have been deleted or not seeded. Restart the app to auto-repair menu data, or ensure GET /api/cafes returns cafes."));

        List<RestaurantTable> tables = tableRepository.findByCafeIdOrderByTableNumberAsc(cafeId);
        if (tables.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tables found for cafe " + cafeId);
        }
        Long tableId = request.getTableId() != null ? request.getTableId() : tables.get(0).getId();
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Table not found"));
        if (!table.getCafeId().equals(cafeId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Table " + tableId + " does not belong to cafe " + cafeId);
        }

        CafeOrder order = new CafeOrder();
        order.setUserId(userId);
        order.setCafeId(cafeId);
        order.setTableId(tableId);
        order.setBookingId(request.getBookingId());
        order.setOrderDate(request.getOrderDate());
        order.setOrderTime(request.getOrderTime());
        order.setStatus(OrderStatus.PLACED);
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest ir : request.getItems()) {
            MenuItem mi = menuItemRepository.findById(ir.getMenuItemId()).orElseThrow();
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
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        CafeOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        boolean isOwner = order.getUserId().equals(userId);
        boolean isStaff = currentUser.getRoleType() == RoleType.ADMIN ||
                ((currentUser.getRoleType() == RoleType.CAFE_OWNER || currentUser.getRoleType() == RoleType.WAITER)
                        && order.getCafeId().equals(currentUser.getCafeId()));

        if (!isOwner && !isStaff) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your order");
        }
        
        String cafeName = cafeRepository.findById(order.getCafeId()).map(Cafe::getName).orElse(null);
        String tableNum = tableRepository.findById(order.getTableId()).map(RestaurantTable::getTableNumber).orElse(null);
        return toOrderResponse(order, currentUser.getName(), cafeName, tableNum);
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
        r.setPaymentPaid(o.getRazorpayPaymentId() != null);
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
