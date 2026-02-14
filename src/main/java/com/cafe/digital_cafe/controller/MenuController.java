package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.dto.MenuItemResponse;
import com.cafe.digital_cafe.entity.MenuCategory;
import com.cafe.digital_cafe.entity.MenuItem;
import com.cafe.digital_cafe.service.MenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Menu API for customers. Requires JWT in Authorization header.
 * GET /api/menu - list all available menu items
 * GET /api/menu?category=beverage - filter by category (beverage, food, dessert, snack)
 */
@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * List menu items. Requires valid JWT (customer, cafe owner, admin, etc.).
     * Optional category filter: beverage, food, dessert, snack
     */
    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> getMenu(
            @RequestParam(required = false) String category) {
        List<MenuItem> items = category != null && !category.isBlank()
                ? menuService.findByCategory(MenuCategory.fromApiValue(category))
                : menuService.findAllAvailable();
        List<MenuItemResponse> response = items.stream()
                .map(MenuItemResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
