package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.dto.CreateMenuItemRequest;
import com.cafe.digital_cafe.dto.MenuItemResponse;
import com.cafe.digital_cafe.dto.UpdateMenuItemRequest;
import com.cafe.digital_cafe.entity.MenuItem;
import com.cafe.digital_cafe.entity.RoleType;
import com.cafe.digital_cafe.entity.User;
import com.cafe.digital_cafe.repository.MenuItemRepository;
import com.cafe.digital_cafe.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CafeOwnerMenuService {

    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;

    public CafeOwnerMenuService(MenuItemRepository menuItemRepository, UserRepository userRepository) {
        this.menuItemRepository = menuItemRepository;
        this.userRepository = userRepository;
    }

    public List<MenuItemResponse> getAllMenuItems() {
        Long cafeId = getCurrentCafeOwnerCafeId();
        return menuItemRepository.findByCafeIdOrderByCategoryAscNameAsc(cafeId).stream()
                .map(MenuItemResponse::from)
                .collect(Collectors.toList());
    }

    public MenuItemResponse createMenuItem(CreateMenuItemRequest request) {
        Long cafeId = getCurrentCafeOwnerCafeId();
        if (menuItemRepository.existsByCafeIdAndName(cafeId, request.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu item with this name already exists in your cafe");
        }
        MenuItem item = new MenuItem(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getCategory()
        );
        item.setCafeId(cafeId);
        item.setAvailable(request.isAvailable());
        item = menuItemRepository.save(item);
        return MenuItemResponse.from(item);
    }

    public MenuItemResponse updateMenuItem(Long id, UpdateMenuItemRequest request) {
        Long cafeId = getCurrentCafeOwnerCafeId();
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found"));
        if (!cafeId.equals(item.getCafeId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Menu item does not belong to your cafe");
        }
        if (request.getName() != null) {
            if (menuItemRepository.existsByCafeIdAndName(cafeId, request.getName()) && !request.getName().equals(item.getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Menu item with this name already exists in your cafe");
            }
            item.setName(request.getName());
        }
        if (request.getDescription() != null) item.setDescription(request.getDescription());
        if (request.getPrice() != null) item.setPrice(request.getPrice());
        if (request.getCategory() != null) item.setCategory(request.getCategory());
        if (request.getAvailable() != null) item.setAvailable(request.getAvailable());
        item = menuItemRepository.save(item);
        return MenuItemResponse.from(item);
    }

    public void deleteMenuItem(Long id) {
        Long cafeId = getCurrentCafeOwnerCafeId();
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu item not found"));
        if (!cafeId.equals(item.getCafeId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Menu item does not belong to your cafe");
        }
        menuItemRepository.deleteById(id);
    }

    private Long getCurrentCafeOwnerCafeId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof String email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (user.getRoleType() != RoleType.CAFE_OWNER || user.getCafeId() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cafe owner access required");
        }
        return user.getCafeId();
    }
}
