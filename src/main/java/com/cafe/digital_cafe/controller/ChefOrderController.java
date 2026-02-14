package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.dto.OrderResponse;
import com.cafe.digital_cafe.dto.UpdateOrderStatusRequest;
import com.cafe.digital_cafe.service.ChefOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chef/orders")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class ChefOrderController {

    private final ChefOrderService chefOrderService;

    public ChefOrderController(ChefOrderService chefOrderService) {
        this.chefOrderService = chefOrderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders() {
        List<OrderResponse> list = chefOrderService.getOrders();
        return ResponseEntity.ok(list);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateOrderStatusRequest request) {
        OrderResponse response = chefOrderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(response);
    }
}
