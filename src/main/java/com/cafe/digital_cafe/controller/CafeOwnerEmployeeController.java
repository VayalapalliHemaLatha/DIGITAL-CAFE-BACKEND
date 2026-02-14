package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.dto.EmployeeResponse;
import com.cafe.digital_cafe.service.CafeOwnerEmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cafeowners")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class CafeOwnerEmployeeController {

    private final CafeOwnerEmployeeService cafeOwnerEmployeeService;

    public CafeOwnerEmployeeController(CafeOwnerEmployeeService cafeOwnerEmployeeService) {
        this.cafeOwnerEmployeeService = cafeOwnerEmployeeService;
    }

    /**
     * Get all waiters. Cafe owner only.
     */
    @GetMapping("/waiters")
    public ResponseEntity<List<EmployeeResponse>> getWaiters() {
        List<EmployeeResponse> list = cafeOwnerEmployeeService.getWaiters();
        return ResponseEntity.ok(list);
    }

    /**
     * Get all chefs. Cafe owner only.
     */
    @GetMapping("/chefs")
    public ResponseEntity<List<EmployeeResponse>> getChefs() {
        List<EmployeeResponse> list = cafeOwnerEmployeeService.getChefs();
        return ResponseEntity.ok(list);
    }
}
