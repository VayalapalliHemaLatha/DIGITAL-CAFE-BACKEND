package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.entity.MenuCategory;
import com.cafe.digital_cafe.entity.MenuItem;
import com.cafe.digital_cafe.repository.MenuItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    public MenuService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> findAllAvailable() {
        return menuItemRepository.findByAvailableTrueOrderByCategoryAscNameAsc();
    }

    public List<MenuItem> findByCategory(MenuCategory category) {
        return menuItemRepository.findByCategoryAndAvailableTrue(category);
    }
}
