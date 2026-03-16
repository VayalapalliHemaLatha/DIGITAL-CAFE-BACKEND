package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.dto.RazorpayOrderResponse;
import com.cafe.digital_cafe.dto.VerifyPaymentRequest;
import com.cafe.digital_cafe.repository.UserRepository;
import com.cafe.digital_cafe.service.RazorpayPaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://localhost:7000", "http://127.0.0.1:7000"})
public class PaymentController {

    private final RazorpayPaymentService paymentService;
    private final UserRepository userRepository;

    public PaymentController(RazorpayPaymentService paymentService, UserRepository userRepository) {
        this.paymentService = paymentService;
        this.userRepository = userRepository;
    }

    @PostMapping("/{orderId}/payment/create")
    public ResponseEntity<RazorpayOrderResponse> createPaymentOrder(@PathVariable("orderId") Long orderId) {
        Long userId = getCurrentUserId();
        RazorpayOrderResponse response = paymentService.createRazorpayOrder(orderId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{orderId}/payment/verify")
    public ResponseEntity<Void> verifyPayment(
            @PathVariable("orderId") Long orderId,
            @Valid @RequestBody VerifyPaymentRequest request) {
        Long userId = getCurrentUserId();
        paymentService.verifyAndCapturePayment(orderId, userId, request);
        return ResponseEntity.ok().build();
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
}
