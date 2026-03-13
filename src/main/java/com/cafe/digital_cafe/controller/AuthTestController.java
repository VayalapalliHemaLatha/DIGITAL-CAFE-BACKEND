package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.entity.User;
import com.cafe.digital_cafe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class AuthTestController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/test-login")
    public ResponseEntity<Map<String, Object>> testLogin(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        // Test admin login
        if ("admin@digitalcafe.com".equals(email) && "password123".equals(password)) {
            User admin = userRepository.findByEmail(email).orElse(null);
            if (admin != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login successful");
                response.put("role", admin.getRoleType());
                response.put("email", admin.getEmail());
                response.put("name", admin.getName());
                return ResponseEntity.ok(response);
            }
        }
        
        // Test waiter login
        if ("waiter@digitalcafe.com".equals(email) && "password123".equals(password)) {
            User waiter = userRepository.findByEmail(email).orElse(null);
            if (waiter != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login successful");
                response.put("role", waiter.getRoleType());
                response.put("email", waiter.getEmail());
                response.put("name", waiter.getName());
                return ResponseEntity.ok(response);
            }
        }
        
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    @GetMapping("/admin-test")
    public ResponseEntity<Map<String, String>> adminTest() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return ResponseEntity.ok(Map.of("message", "Admin access confirmed", "user", auth.getName()));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
    }

    @GetMapping("/waiter-test")
    public ResponseEntity<Map<String, String>> waiterTest() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return ResponseEntity.ok(Map.of("message", "Waiter access confirmed", "user", auth.getName()));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
    }
}
