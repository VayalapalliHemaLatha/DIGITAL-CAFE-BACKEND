package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.dto.CartItemRequest;
import com.cafe.digital_cafe.dto.SimpleOrderRequest;
import com.cafe.digital_cafe.dto.PaymentVerifyRequest;
import com.cafe.digital_cafe.entity.MenuItem;
import com.cafe.digital_cafe.entity.CafeOrder;
import com.cafe.digital_cafe.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class OrderController {

    @Autowired
    private MenuService menuService;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private RazorpayService razorpayService;

    @PostMapping("/cart/add")
    public ResponseEntity<String> addToCart(@RequestBody CartItemRequest request) {
        cartService.addToCart(request);
        return ResponseEntity.ok("Item added to cart");
    }

    @PostMapping("/order/create")
    public ResponseEntity<CafeOrder> createOrder(@RequestBody SimpleOrderRequest request) {
        CafeOrder order = orderService.createOrder(request);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/payment/create-razorpay-order")
    public ResponseEntity<Map<String, Object>> createPaymentOrder(@RequestParam Long orderId) {
        try {
            CafeOrder order = orderService.getOrderById(orderId);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }
            
            int amount = order.getTotalAmount().intValue();
            Map<String, Object> response = razorpayService.createOrder(
                amount, 
                "INR", 
                "order_rcptid_" + orderId
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create payment order"));
        }
    }

    @PostMapping("/payment/verify")
    public ResponseEntity<String> verifyPayment(@RequestBody PaymentVerifyRequest request) {
        boolean isValid = razorpayService.verifyPayment(
            request.getRazorpayOrderId(),
            request.getRazorpayPaymentId(),
            request.getRazorpaySignature()
        );
        
        if (isValid) {
            String orderIdStr = request.getRazorpayOrderId().replace("order_rcptid_", "");
            Long orderId = Long.parseLong(orderIdStr);
            
            orderService.updatePaymentStatus(orderId, "SUCCESS", request.getRazorpayPaymentId());
            return ResponseEntity.ok("Payment verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Payment verification failed");
        }
    }

    @GetMapping("/orders/customer/{id}")
    public ResponseEntity<List<CafeOrder>> getCustomerOrders(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getCustomerOrders(id));
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<List<CafeOrder>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
