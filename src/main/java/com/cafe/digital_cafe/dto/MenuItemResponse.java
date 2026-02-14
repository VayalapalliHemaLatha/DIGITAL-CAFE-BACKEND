package com.cafe.digital_cafe.dto;

import com.cafe.digital_cafe.entity.MenuCategory;
import com.cafe.digital_cafe.entity.MenuItem;

import java.math.BigDecimal;

public class MenuItemResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private MenuCategory category;
    private boolean available;

    public MenuItemResponse() {
    }

    public MenuItemResponse(Long id, String name, String description, BigDecimal price,
                            MenuCategory category, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.available = available;
    }

    public static MenuItemResponse from(MenuItem item) {
        return new MenuItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getCategory(),
                item.isAvailable()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public MenuCategory getCategory() { return category; }
    public void setCategory(MenuCategory category) { this.category = category; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
