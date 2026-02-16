package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.dto.AuthResponse;
import com.cafe.digital_cafe.dto.LoginRequest;
import com.cafe.digital_cafe.dto.SignupRequest;
import com.cafe.digital_cafe.entity.RoleType;
import com.cafe.digital_cafe.entity.User;
import com.cafe.digital_cafe.repository.CafeRepository;
import com.cafe.digital_cafe.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CafeRepository cafeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, CafeRepository cafeRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.cafeRepository = cafeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Signup: self-registration (customer only) or staff creation by admin/cafeowner.
     * - Self-registration (no JWT): roleType must be customer or omitted.
     * - Admin: can only create cafeowner.
     * - Cafe owner: can only create chef or waiter.
     */
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadCredentialsException("Email already registered");
        }
        RoleType effectiveRole = request.getRoleType() != null ? request.getRoleType() : RoleType.CUSTOMER;
        User creator = getCurrentAuthenticatedUser();
        validateRoleCreation(creator, effectiveRole, request.getCafeId());
        Long cafeId = resolveCafeId(creator, effectiveRole, request.getCafeId());
        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhone(),
                request.getAddress(),
                effectiveRole
        );
        user.setCafeId(cafeId);
        user = userRepository.save(user);
        String token = jwtService.generateToken(user.getEmail(), user.getId(), user.getRoleType());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getName(), user.getRoleType());
    }

    private User getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (!(principal instanceof String email)) return null;
        return userRepository.findByEmail(email).orElse(null);
    }

    private void validateRoleCreation(User creator, RoleType targetRole, Long cafeId) {
        if (creator == null) {
            if (targetRole != RoleType.CUSTOMER) {
                throw new BadCredentialsException("Self-registration only allows customer role. Use admin/cafe owner to create staff.");
            }
            return;
        }
        RoleType creatorRole = creator.getRoleType();
        if (creatorRole == RoleType.ADMIN) {
            if (targetRole != RoleType.CAFE_OWNER) {
                throw new BadCredentialsException("Admin can only create cafe owner accounts.");
            }
            if (cafeId == null) {
                throw new BadCredentialsException("Cafe ID is required when creating cafe owner. Specify which cafe they manage.");
            }
            if (!cafeRepository.existsById(cafeId)) {
                throw new BadCredentialsException("Cafe not found.");
            }
        } else if (creatorRole == RoleType.CAFE_OWNER) {
            if (targetRole != RoleType.CHEF && targetRole != RoleType.WAITER) {
                throw new BadCredentialsException("Cafe owner can only create chef or waiter accounts.");
            }
            if (creator.getCafeId() == null) {
                throw new BadCredentialsException("Cafe owner has no cafe assigned.");
            }
        } else {
            throw new BadCredentialsException("Only admin or cafe owner can create staff accounts.");
        }
    }

    private Long resolveCafeId(User creator, RoleType targetRole, Long requestCafeId) {
        if (targetRole == RoleType.CAFE_OWNER && creator != null && creator.getRoleType() == RoleType.ADMIN) {
            return requestCafeId;
        }
        if (targetRole == RoleType.CHEF || targetRole == RoleType.WAITER) {
            return creator != null ? creator.getCafeId() : null;
        }
        return null;
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        if (!user.isActive()) {
            throw new BadCredentialsException("Account is deactivated. Contact admin.");
        }
        String token = jwtService.generateToken(user.getEmail(), user.getId(), user.getRoleType());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getName(), user.getRoleType());
    }

    /**
     * Forgot password: reset password by email. No JWT required.
     * Works for all roles (customer, chef, waiter, cafe owner, admin).
     * If email does not exist, throws error.
     */
    public void forgotPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email.trim())
                .orElseThrow(() -> new BadCredentialsException("No account found with this email"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
