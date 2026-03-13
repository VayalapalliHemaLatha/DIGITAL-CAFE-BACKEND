package com.cafe.digital_cafe.dto;

import java.math.BigDecimal;

public class CartItemRequest {
    
    private Long menuItemId;
    private Integer quantity;
    
    public CartItemRequest() {}
    
    public CartItemRequest(Long menuItemId, Integer quantity) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
    }
    
    public Long getMenuItemId() { return menuItemId; }
    public void setMenuItemId(Long menuItemId) { this.menuItemId = menuItemId; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
