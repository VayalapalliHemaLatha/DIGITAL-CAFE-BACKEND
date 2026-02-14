package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.dto.CafeDetailResponse;
import com.cafe.digital_cafe.dto.CafeResponse;
import com.cafe.digital_cafe.service.CafeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cafes")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class CafeController {

    private final CafeService cafeService;

    public CafeController(CafeService cafeService) {
        this.cafeService = cafeService;
    }

    @GetMapping
    public ResponseEntity<List<CafeResponse>> getAllCafes() {
        List<CafeResponse> list = cafeService.getAllCafes();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CafeDetailResponse> getCafeDetail(@PathVariable Long id) {
        CafeDetailResponse detail = cafeService.getCafeDetail(id);
        return ResponseEntity.ok(detail);
    }
}
