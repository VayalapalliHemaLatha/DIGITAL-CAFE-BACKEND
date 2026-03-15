package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.entity.MenuItem;
import com.cafe.digital_cafe.entity.MenuCategory;
import com.cafe.digital_cafe.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findByAvailableTrueOrderByCategoryAscNameAsc();
    }

    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id).orElse(null);
    }

    public List<MenuItem> getMenuItemsByCategory(MenuCategory category) {
        return menuItemRepository.findByCategoryAndAvailableTrue(category);
    }

    public List<MenuItem> getMenuByCafe(Long cafeId) {
        return menuItemRepository.findByCafeIdAndAvailableTrueOrderByCategoryAscNameAsc(cafeId);
    }

    public List<MenuItem> getMenuByCafeAndCategory(Long cafeId, MenuCategory category) {
        return menuItemRepository.findByCafeIdAndCategoryAndAvailableTrueOrderByNameAsc(cafeId, category);
    }
}
