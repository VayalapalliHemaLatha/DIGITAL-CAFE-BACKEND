package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.dto.UserResponse;
import com.cafe.digital_cafe.entity.User;
import com.cafe.digital_cafe.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get all users. Returns ID, Name, Email, Phone, Address (no password).
     * Requires valid JWT in Authorization header.
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> list = userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getPhone(),
                u.getAddress(),
                u.getRoleType()
        );
    }
}
