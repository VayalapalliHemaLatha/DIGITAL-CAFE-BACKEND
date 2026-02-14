package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.dto.OrderResponse;
import com.cafe.digital_cafe.service.CafeOwnerOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cafeowners/orders")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class CafeOwnerOrderController {

    private final CafeOwnerOrderService cafeOwnerOrderService;

    public CafeOwnerOrderController(CafeOwnerOrderService cafeOwnerOrderService) {
        this.cafeOwnerOrderService = cafeOwnerOrderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders() {
        List<OrderResponse> list = cafeOwnerOrderService.getOrders();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        OrderResponse response = cafeOwnerOrderService.getOrder(id);
        return ResponseEntity.ok(response);
    }
}
