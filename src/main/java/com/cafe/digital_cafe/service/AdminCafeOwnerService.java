package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.dto.CafeOwnerResponse;
import com.cafe.digital_cafe.dto.UpdateCafeOwnerStatusRequest;
import com.cafe.digital_cafe.entity.Cafe;
import com.cafe.digital_cafe.entity.RoleType;
import com.cafe.digital_cafe.entity.User;
import com.cafe.digital_cafe.repository.CafeRepository;
import com.cafe.digital_cafe.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminCafeOwnerService {

    private final UserRepository userRepository;
    private final CafeRepository cafeRepository;

    public AdminCafeOwnerService(UserRepository userRepository, CafeRepository cafeRepository) {
        this.userRepository = userRepository;
        this.cafeRepository = cafeRepository;
    }

    public List<CafeOwnerResponse> getAllCafeOwners() {
        ensureAdmin();
        return userRepository.findByRoleTypeOrderByNameAsc(RoleType.CAFE_OWNER)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CafeOwnerResponse updateCafeOwnerStatus(Long id, UpdateCafeOwnerStatusRequest request) {
        ensureAdmin();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cafe owner not found"));
        if (user.getRoleType() != RoleType.CAFE_OWNER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a cafe owner");
        }
        user.setActive(request.isActive());
        user = userRepository.save(user);
        return toResponse(user);
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

    private CafeOwnerResponse toResponse(User u) {
        String cafeName = u.getCafeId() != null
                ? cafeRepository.findById(u.getCafeId()).map(Cafe::getName).orElse(null)
                : null;
        return new CafeOwnerResponse(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getPhone(),
                u.getAddress(),
                u.getRoleType(),
                u.isActive(),
                u.getCafeId(),
                cafeName
        );
    }
}
