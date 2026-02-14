package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.dto.OrderResponse;
import com.cafe.digital_cafe.dto.UpdateOrderStatusRequest;
import com.cafe.digital_cafe.service.WaiterOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/waiter/orders")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class WaiterOrderController {

    private final WaiterOrderService waiterOrderService;

    public WaiterOrderController(WaiterOrderService waiterOrderService) {
        this.waiterOrderService = waiterOrderService;
    }

    @GetMapping("/ready")
    public ResponseEntity<List<OrderResponse>> getReadyOrders() {
        List<OrderResponse> list = waiterOrderService.getReadyOrders();
        return ResponseEntity.ok(list);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> list = waiterOrderService.getAllOrders();
        return ResponseEntity.ok(list);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> markAsServed(
            @PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse response = waiterOrderService.markAsServed(id, request);
        return ResponseEntity.ok(response);
    }
}
