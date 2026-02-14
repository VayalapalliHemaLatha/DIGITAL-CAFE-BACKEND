package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.entity.MenuCategory;
import com.cafe.digital_cafe.entity.MenuItem;
import com.cafe.digital_cafe.repository.MenuItemRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    public MenuService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> findByCafeId(Long cafeId) {
        if (cafeId == null) return Collections.emptyList();
        return menuItemRepository.findByCafeIdAndAvailableTrueOrderByCategoryAscNameAsc(cafeId);
    }

    public List<MenuItem> findByCafeIdAndCategory(Long cafeId, MenuCategory category) {
        if (cafeId == null || category == null) return Collections.emptyList();
        return menuItemRepository.findByCafeIdAndCategoryAndAvailableTrue(cafeId, category);
    }
}
