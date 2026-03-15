package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.dto.SimpleOrderRequest;
import com.cafe.digital_cafe.entity.*;
import com.cafe.digital_cafe.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {
    
    @Autowired
    private CafeOrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private MenuItemRepository menuItemRepository;
    
    public CafeOrder createOrder(SimpleOrderRequest request) {
        BigDecimal totalAmount = cartService.calculateTotal();
        
        CafeOrder order = new CafeOrder();
        order.setUserId(request.getCustomerId());
        order.setTotalAmount(totalAmount);
        order.setPaymentStatus("PENDING");
        
        order = orderRepository.save(order);
        
        for (var cartItem : cartService.getCartItems()) {
            MenuItem menuItem = menuItemRepository.findById(cartItem.getMenuItemId()).orElse(null);
            if (menuItem != null) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setMenuItemId(menuItem.getId());
                orderItem.setItemName(menuItem.getName());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setUnitPrice(menuItem.getPrice());
                orderItemRepository.save(orderItem);
            }
        }
        
        cartService.clearCart();
        return order;
    }
    
    public CafeOrder updatePaymentStatus(Long orderId, String paymentStatus, String paymentId) {
        CafeOrder order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
                order.setPaymentStatus(paymentStatus);
            order = orderRepository.save(order);
        }
        return order;
    }
    
    public List<CafeOrder> getCustomerOrders(Long customerId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(customerId);
    }
    
    public List<CafeOrder> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public CafeOrder getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }
}
