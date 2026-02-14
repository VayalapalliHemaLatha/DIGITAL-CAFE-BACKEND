package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.dto.*;
import com.cafe.digital_cafe.entity.Cafe;
import com.cafe.digital_cafe.entity.MenuItem;
import com.cafe.digital_cafe.entity.RestaurantTable;
import com.cafe.digital_cafe.repository.CafeRepository;
import com.cafe.digital_cafe.repository.MenuItemRepository;
import com.cafe.digital_cafe.repository.RestaurantTableRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CafeService {

    private final CafeRepository cafeRepository;
    private final RestaurantTableRepository tableRepository;
    private final MenuItemRepository menuItemRepository;

    public CafeService(CafeRepository cafeRepository, RestaurantTableRepository tableRepository,
                       MenuItemRepository menuItemRepository) {
        this.cafeRepository = cafeRepository;
        this.tableRepository = tableRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public List<CafeResponse> getAllCafes() {
        return cafeRepository.findAllByOrderByNameAsc().stream()
                .map(c -> new CafeResponse(c.getId(), c.getName(), c.getAddress(), c.getPhone()))
                .collect(Collectors.toList());
    }

    public CafeDetailResponse getCafeDetail(Long cafeId) {
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cafe not found"));
        List<RestaurantTable> tables = tableRepository.findByCafeIdOrderByTableNumberAsc(cafeId);
        List<MenuItem> menuItems = menuItemRepository.findByCafeIdAndAvailableTrueOrderByCategoryAscNameAsc(cafeId);
        CafeDetailResponse r = new CafeDetailResponse();
        r.setId(cafe.getId());
        r.setName(cafe.getName());
        r.setAddress(cafe.getAddress());
        r.setPhone(cafe.getPhone());
        r.setTableCount(tables.size());
        r.setTables(tables.stream().map(t -> new TableResponse(t.getId(), t.getTableNumber(), t.getCapacity(), t.getStatus())).collect(Collectors.toList()));
        r.setMenu(menuItems.stream().map(MenuItemResponse::from).collect(Collectors.toList()));
        return r;
    }
}
