package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.dto.CreateMenuItemRequest;
import com.cafe.digital_cafe.dto.MenuItemResponse;
import com.cafe.digital_cafe.dto.UpdateMenuItemRequest;
import com.cafe.digital_cafe.service.CafeOwnerMenuService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cafeowners/menu")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class CafeOwnerMenuController {

    private final CafeOwnerMenuService cafeOwnerMenuService;

    public CafeOwnerMenuController(CafeOwnerMenuService cafeOwnerMenuService) {
        this.cafeOwnerMenuService = cafeOwnerMenuService;
    }

    /**
     * Get all menu items (including unavailable). Cafe owner only.
     */
    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> getAllMenuItems() {
        List<MenuItemResponse> list = cafeOwnerMenuService.getAllMenuItems();
        return ResponseEntity.ok(list);
    }

    /**
     * Add new menu item. Cafe owner only.
     */
    @PostMapping
    public ResponseEntity<MenuItemResponse> createMenuItem(@Valid @RequestBody CreateMenuItemRequest request) {
        MenuItemResponse response = cafeOwnerMenuService.createMenuItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update menu item. Cafe owner only.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable Long id,
            @RequestBody UpdateMenuItemRequest request) {
        MenuItemResponse response = cafeOwnerMenuService.updateMenuItem(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Remove menu item. Cafe owner only.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        cafeOwnerMenuService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}
