package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.dto.*;
import com.cafe.digital_cafe.service.CafeOwnerTableService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cafeowners/tables")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class CafeOwnerTableController {

    private final CafeOwnerTableService cafeOwnerTableService;

    public CafeOwnerTableController(CafeOwnerTableService cafeOwnerTableService) {
        this.cafeOwnerTableService = cafeOwnerTableService;
    }

    /**
     * Get all tables with available/booked counts. Cafe owner only.
     */
    @GetMapping
    public ResponseEntity<TablesSummaryResponse> getAllTables() {
        TablesSummaryResponse response = cafeOwnerTableService.getAllTables();
        return ResponseEntity.ok(response);
    }

    /**
     * Add new table. Cafe owner only.
     */
    @PostMapping
    public ResponseEntity<TableResponse> createTable(@RequestBody CreateTableRequest request) {
        TableResponse response = cafeOwnerTableService.createTable(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update table. Cafe owner only.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TableResponse> updateTable(
            @PathVariable Long id,
            @RequestBody UpdateTableRequest request) {
        TableResponse response = cafeOwnerTableService.updateTable(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Update table status (available/booked). Cafe owner only.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TableResponse> updateTableStatus(
            @PathVariable Long id,
            @RequestBody UpdateTableStatusRequest request) {
        TableResponse response = cafeOwnerTableService.updateTableStatus(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Remove table. Cafe owner only.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        cafeOwnerTableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }
}
