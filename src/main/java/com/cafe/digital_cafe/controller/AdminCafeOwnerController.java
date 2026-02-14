package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.dto.CafeOwnerResponse;
import com.cafe.digital_cafe.dto.UpdateCafeOwnerStatusRequest;
import com.cafe.digital_cafe.service.AdminCafeOwnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cafeowners")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class AdminCafeOwnerController {

    private final AdminCafeOwnerService adminCafeOwnerService;

    public AdminCafeOwnerController(AdminCafeOwnerService adminCafeOwnerService) {
        this.adminCafeOwnerService = adminCafeOwnerService;
    }

    /**
     * Get all cafe owners. Admin only.
     */
    @GetMapping
    public ResponseEntity<List<CafeOwnerResponse>> getAllCafeOwners() {
        List<CafeOwnerResponse> list = adminCafeOwnerService.getAllCafeOwners();
        return ResponseEntity.ok(list);
    }

    /**
     * Update cafe owner active status (activate/deactivate). Admin only.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<CafeOwnerResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateCafeOwnerStatusRequest request) {
        CafeOwnerResponse response = adminCafeOwnerService.updateCafeOwnerStatus(id, request);
        return ResponseEntity.ok(response);
    }
}
