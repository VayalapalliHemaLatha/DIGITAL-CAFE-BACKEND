package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.entity.CafeOrder;
import com.cafe.digital_cafe.repository.CafeOrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class PaymentController {

    private final CafeOrderRepository orderRepository;

    public PaymentController(CafeOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @PostMapping("/pay/{orderId}")
    public ResponseEntity<Map<String, String>> payOrder(@PathVariable Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    order.setPaymentStatus("PAID");
                    orderRepository.save(order);
                    return ResponseEntity.ok(Map.of("message", "Payment Successful"));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Order not found")));
    }
}
