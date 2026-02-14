package com.cafe.digital_cafe.config;

import com.cafe.digital_cafe.entity.User;
import com.cafe.digital_cafe.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // already has data
        }

        User u1 = new User(
                "John Doe",
                "john@digitalcafe.com",
                passwordEncoder.encode("password123"),
                "+1-555-0101",
                "123 Main St, New York, NY"
        );
        User u2 = new User(
                "Jane Smith",
                "jane@digitalcafe.com",
                passwordEncoder.encode("password123"),
                "+1-555-0102",
                "456 Oak Ave, Los Angeles, CA"
        );
        User u3 = new User(
                "Bob Wilson",
                "bob@digitalcafe.com",
                passwordEncoder.encode("password123"),
                "+1-555-0103",
                "789 Pine Rd, Chicago, IL"
        );

        userRepository.save(u1);
        userRepository.save(u2);
        userRepository.save(u3);

        System.out.println("  Sample users inserted (email/password: john@digitalcafe.com / password123, etc.)");
    }
}
