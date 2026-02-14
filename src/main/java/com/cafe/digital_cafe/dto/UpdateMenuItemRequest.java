package com.cafe.digital_cafe.dto;

import com.cafe.digital_cafe.entity.MenuCategory;

import java.math.BigDecimal;

/**
 * All fields optional - only non-null fields are updated.
 */
public class UpdateMenuItemRequest {

    private String name;
    private String description;
    private BigDecimal price;
    private MenuCategory category;
    private Boolean available;

    public UpdateMenuItemRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public MenuCategory getCategory() {
        return category;
    }

    public void setCategory(MenuCategory category) {
        this.category = category;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
