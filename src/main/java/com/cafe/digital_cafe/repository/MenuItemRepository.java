package com.cafe.digital_cafe.repository;

import com.cafe.digital_cafe.entity.MenuCategory;
import com.cafe.digital_cafe.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByAvailableTrueOrderByCategoryAscNameAsc();

    List<MenuItem> findByCategoryAndAvailableTrue(MenuCategory category);
}
