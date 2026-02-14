package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.dto.ProfileResponse;
import com.cafe.digital_cafe.dto.UpdateProfileRequest;
import com.cafe.digital_cafe.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Update own profile. JWT required. All fields optional.
     * PUT /api/users/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<ProfileResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        ProfileResponse response = userService.updateProfile(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get own profile. JWT required.
     * GET /api/users/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile() {
        ProfileResponse response = userService.getProfile();
        return ResponseEntity.ok(response);
    }
}
