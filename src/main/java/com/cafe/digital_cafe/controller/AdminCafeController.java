package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.dto.CafeResponse;
import com.cafe.digital_cafe.dto.CreateCafeRequest;
import com.cafe.digital_cafe.dto.UpdateCafeRequest;
import com.cafe.digital_cafe.service.AdminCafeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cafes")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class AdminCafeController {

    private final AdminCafeService adminCafeService;

    public AdminCafeController(AdminCafeService adminCafeService) {
        this.adminCafeService = adminCafeService;
    }

    @GetMapping
    public ResponseEntity<List<CafeResponse>> getAllCafes() {
        List<CafeResponse> list = adminCafeService.getAllCafes();
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<CafeResponse> createCafe(@Valid @RequestBody CreateCafeRequest request) {
        CafeResponse response = adminCafeService.createCafe(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CafeResponse> updateCafe(
            @PathVariable Long id,
            @RequestBody UpdateCafeRequest request) {
        CafeResponse response = adminCafeService.updateCafe(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCafe(@PathVariable Long id) {
        adminCafeService.deleteCafe(id);
        return ResponseEntity.noContent().build();
    }
}
