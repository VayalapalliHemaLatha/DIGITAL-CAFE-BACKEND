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
 * Menu API for customers. Requires JWT.
 * GET /api/menu - list all available menu items (all cafes)
 * GET /api/menu?cafeId=1 - list available menu items for a specific cafe
 * GET /api/menu?cafeId=1&category=beverage - filter by category
 */
@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://localhost:7000", "http://127.0.0.1:7000"})
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> getMenu(
            @RequestParam(required = false) Long cafeId,
            @RequestParam(required = false) String category) {
        List<MenuItem> items;
        if (cafeId != null) {
            items = category != null && !category.isBlank()
                    ? menuService.findByCafeIdAndCategory(cafeId, MenuCategory.fromApiValue(category))
                    : menuService.findByCafeId(cafeId);
        } else {
            MenuCategory cat = category != null && !category.isBlank() ? MenuCategory.fromApiValue(category) : null;
            items = cat != null ? menuService.findAllAvailableByCategory(cat) : menuService.findAllAvailable();
        }
        List<MenuItemResponse> response = items.stream()
                .map(MenuItemResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
