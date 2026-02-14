package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.dto.EmployeeResponse;
import com.cafe.digital_cafe.entity.RoleType;
import com.cafe.digital_cafe.entity.User;
import com.cafe.digital_cafe.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CafeOwnerEmployeeService {

    private final UserRepository userRepository;

    public CafeOwnerEmployeeService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<EmployeeResponse> getWaiters() {
        User cafeOwner = getCurrentCafeOwner();
        return userRepository.findByRoleTypeAndCafeIdOrderByNameAsc(RoleType.WAITER, cafeOwner.getCafeId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<EmployeeResponse> getChefs() {
        User cafeOwner = getCurrentCafeOwner();
        return userRepository.findByRoleTypeAndCafeIdOrderByNameAsc(RoleType.CHEF, cafeOwner.getCafeId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private User getCurrentCafeOwner() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof String email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (user.getRoleType() != RoleType.CAFE_OWNER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cafe owner access required");
        }
        if (user.getCafeId() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cafe owner has no cafe assigned");
        }
        return user;
    }

    private EmployeeResponse toResponse(User u) {
        return new EmployeeResponse(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getPhone(),
                u.getAddress(),
                u.getRoleType(),
                u.isActive()
        );
    }
}
