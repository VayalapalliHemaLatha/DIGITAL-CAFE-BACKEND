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
        ensureCafeOwner();
        return userRepository.findByRoleTypeOrderByNameAsc(RoleType.WAITER)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<EmployeeResponse> getChefs() {
        ensureCafeOwner();
        return userRepository.findByRoleTypeOrderByNameAsc(RoleType.CHEF)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void ensureCafeOwner() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        boolean isCafeOwner = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_CAFE_OWNER".equals(a.getAuthority()));
        if (!isCafeOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cafe owner access required");
        }
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
