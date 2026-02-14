package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.dto.CafeResponse;
import com.cafe.digital_cafe.dto.CreateCafeRequest;
import com.cafe.digital_cafe.dto.UpdateCafeRequest;
import com.cafe.digital_cafe.entity.Cafe;
import com.cafe.digital_cafe.repository.CafeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminCafeService {

    private final CafeRepository cafeRepository;

    public AdminCafeService(CafeRepository cafeRepository) {
        this.cafeRepository = cafeRepository;
    }

    public List<CafeResponse> getAllCafes() {
        ensureAdmin();
        return cafeRepository.findAllByOrderByNameAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CafeResponse createCafe(CreateCafeRequest request) {
        ensureAdmin();
        Cafe cafe = new Cafe(request.getName(), request.getAddress(), request.getPhone());
        cafe = cafeRepository.save(cafe);
        return toResponse(cafe);
    }

    public CafeResponse updateCafe(Long id, UpdateCafeRequest request) {
        ensureAdmin();
        Cafe cafe = cafeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cafe not found"));
        if (request.getName() != null) cafe.setName(request.getName());
        if (request.getAddress() != null) cafe.setAddress(request.getAddress());
        if (request.getPhone() != null) cafe.setPhone(request.getPhone());
        cafe = cafeRepository.save(cafe);
        return toResponse(cafe);
    }

    public void deleteCafe(Long id) {
        ensureAdmin();
        if (!cafeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cafe not found");
        }
        cafeRepository.deleteById(id);
    }

    private void ensureAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
        }
    }

    private CafeResponse toResponse(Cafe c) {
        return new CafeResponse(c.getId(), c.getName(), c.getAddress(), c.getPhone());
    }
}
