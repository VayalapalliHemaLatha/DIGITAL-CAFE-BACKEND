package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.dto.CartItemRequest;
import com.cafe.digital_cafe.entity.MenuItem;
import com.cafe.digital_cafe.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {
    
    @Autowired
    private MenuItemRepository menuItemRepository;
    
    private List<CartItemRequest> cartItems = new ArrayList<>();
    
    public void addToCart(CartItemRequest cartItem) {
        MenuItem menuItem = menuItemRepository.findById(cartItem.getMenuItemId()).orElse(null);
        if (menuItem != null && menuItem.isAvailable()) {
            cartItems.add(cartItem);
        }
    }
    
    public List<CartItemRequest> getCartItems() {
        return cartItems;
    }
    
    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItemRequest item : cartItems) {
            MenuItem menuItem = menuItemRepository.findById(item.getMenuItemId()).orElse(null);
            if (menuItem != null) {
                total = total.add(menuItem.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }
        return total;
    }
    
    public void clearCart() {
        cartItems.clear();
    }
}
