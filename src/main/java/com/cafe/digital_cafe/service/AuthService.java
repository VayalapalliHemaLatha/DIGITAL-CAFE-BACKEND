package com.cafe.digital_cafe.service;

import com.cafe.digital_cafe.dto.AuthResponse;
import com.cafe.digital_cafe.dto.LoginRequest;
import com.cafe.digital_cafe.dto.SignupRequest;
import com.cafe.digital_cafe.entity.User;
import com.cafe.digital_cafe.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadCredentialsException("Email already registered");
        }
        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhone(),
                request.getAddress()
        );
        user = userRepository.save(user);
        String token = jwtService.generateToken(user.getEmail(), user.getId());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getName());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        String token = jwtService.generateToken(user.getEmail(), user.getId());
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getName());
    }
}
