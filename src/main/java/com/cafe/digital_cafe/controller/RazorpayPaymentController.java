package com.cafe.digital_cafe.controller;

import com.razorpay.RazorpayException;
import com.cafe.digital_cafe.service.RazorpayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class RazorpayPaymentController {

    @Autowired
    private RazorpayService razorpayService;

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        return ResponseEntity.ok(Map.of("status", "Payment API Working"));
    }

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder() {
        try {
            // Temporarily disabled - return mock response for testing
            Map<String, Object> mockResponse = Map.of(
                "orderId", "order_mock_test_12345",
                "amount", 50000,
                "currency", "INR"
            );
            return ResponseEntity.ok(mockResponse);
            
            // Original Razorpay code (commented out)
            // Map<String, Object> response = razorpayService.createOrder(50000, "INR", "order_rcptid_11");
            // return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create payment order", "message", e.getMessage()));
        }
    }
}

