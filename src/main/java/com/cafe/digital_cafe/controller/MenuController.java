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
 * Menu API for customers. Requires JWT. Use cafeId to browse a specific cafe's menu.
 * GET /api/menu?cafeId=1 - list available menu items for cafe
 * GET /api/menu?cafeId=1&category=beverage - filter by category
 */
@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> getMenu(
            @RequestParam Long cafeId,
            @RequestParam(required = false) String category) {
        List<MenuItem> items = category != null && !category.isBlank()
                ? menuService.findByCafeIdAndCategory(cafeId, MenuCategory.fromApiValue(category))
                : menuService.findByCafeId(cafeId);
        List<MenuItemResponse> response = items.stream()
                .map(MenuItemResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
