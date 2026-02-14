package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.dto.*;
import com.cafe.digital_cafe.entity.RestaurantTable;
import com.cafe.digital_cafe.entity.RoleType;
import com.cafe.digital_cafe.entity.TableStatus;
import com.cafe.digital_cafe.entity.User;
import com.cafe.digital_cafe.repository.RestaurantTableRepository;
import com.cafe.digital_cafe.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CafeOwnerTableService {

    private final RestaurantTableRepository tableRepository;
    private final UserRepository userRepository;

    public CafeOwnerTableService(RestaurantTableRepository tableRepository, UserRepository userRepository) {
        this.tableRepository = tableRepository;
        this.userRepository = userRepository;
    }

    public TablesSummaryResponse getAllTables() {
        Long cafeId = getCurrentCafeOwnerCafeId();
        List<TableResponse> tables = tableRepository.findByCafeIdOrderByTableNumberAsc(cafeId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        long availableCount = tableRepository.countByCafeIdAndStatus(cafeId, TableStatus.AVAILABLE);
        long bookedCount = tableRepository.countByCafeIdAndStatus(cafeId, TableStatus.BOOKED);
        return new TablesSummaryResponse(tables, availableCount, bookedCount);
    }

    public TableResponse createTable(CreateTableRequest request) {
        Long cafeId = getCurrentCafeOwnerCafeId();
        if (request.getTableNumber() == null || request.getTableNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Table number is required");
        }
        String tableNum = request.getTableNumber().trim();
        if (tableRepository.existsByCafeIdAndTableNumber(cafeId, tableNum)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Table number already exists in your cafe");
        }
        RestaurantTable table = new RestaurantTable(
                cafeId,
                tableNum,
                request.getCapacity() > 0 ? request.getCapacity() : 4
        );
        if (request.getStatus() != null) {
            table.setStatus(request.getStatus());
        }
        table = tableRepository.save(table);
        return toResponse(table);
    }

    public TableResponse updateTable(Long id, UpdateTableRequest request) {
        Long cafeId = getCurrentCafeOwnerCafeId();
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Table not found"));
        if (!cafeId.equals(table.getCafeId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Table does not belong to your cafe");
        }
        if (request.getTableNumber() != null && !request.getTableNumber().isBlank()) {
            String num = request.getTableNumber().trim();
            if (!num.equals(table.getTableNumber()) && tableRepository.existsByCafeIdAndTableNumber(cafeId, num)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Table number already exists in your cafe");
            }
            table.setTableNumber(num);
        }
        if (request.getCapacity() != null && request.getCapacity() > 0) {
            table.setCapacity(request.getCapacity());
        }
        if (request.getStatus() != null) {
            table.setStatus(request.getStatus());
        }
        table = tableRepository.save(table);
        return toResponse(table);
    }

    public TableResponse updateTableStatus(Long id, UpdateTableStatusRequest request) {
        Long cafeId = getCurrentCafeOwnerCafeId();
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Table not found"));
        if (!cafeId.equals(table.getCafeId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Table does not belong to your cafe");
        }
        if (request.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }
        table.setStatus(request.getStatus());
        table = tableRepository.save(table);
        return toResponse(table);
    }

    public void deleteTable(Long id) {
        Long cafeId = getCurrentCafeOwnerCafeId();
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Table not found"));
        if (!cafeId.equals(table.getCafeId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Table does not belong to your cafe");
        }
        tableRepository.deleteById(id);
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

    private TableResponse toResponse(RestaurantTable t) {
        return new TableResponse(t.getId(), t.getTableNumber(), t.getCapacity(), t.getStatus());
    }
}
