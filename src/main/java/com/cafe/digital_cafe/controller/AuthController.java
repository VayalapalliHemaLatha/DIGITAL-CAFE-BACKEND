package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.dto.AuthResponse;
import com.cafe.digital_cafe.dto.ForgotPasswordRequest;
import com.cafe.digital_cafe.dto.LoginRequest;
import com.cafe.digital_cafe.dto.SignupRequest;
import com.cafe.digital_cafe.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        AuthResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Forgot password. No JWT required. For all roles (customer, chef, waiter, cafe owner, admin).
     * If email exists, password is updated; otherwise 401 with error message.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail(), request.getNewPassword());
        return ResponseEntity.ok(new ForgotPasswordResponse("Password reset successfully. You can now login with your new password."));
    }

    /**
     * Logout. Client should discard the JWT after calling this.
     * With stateless JWT there is no server-side session to invalidate.
     */
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout() {
        return ResponseEntity.ok(new LogoutResponse("Logged out successfully"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorBody> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorBody(e.getMessage()));
    }

    public record ErrorBody(String message) {}

    public record ForgotPasswordResponse(String message) {}

    public record LogoutResponse(String message) {}
}
