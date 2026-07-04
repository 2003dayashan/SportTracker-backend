package com.example.SportsTracker;

import com.example.SportsTracker.core.model.Role;
import com.example.SportsTracker.core.model.User;
import com.example.SportsTracker.core.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class SeedAdminTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void seedAdminUser() {
        String email = "admin@test.com";
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .email(email)
                    .password(passwordEncoder.encode("password123"))
                    .roles(List.of(Role.ROLE_ADMIN))
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            System.out.println("Seeded default admin user: " + email);
        } else {
            User admin = existing.get();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("password123"));
            admin.setRoles(List.of(Role.ROLE_ADMIN));
            admin.setEnabled(true);
            userRepository.save(admin);
            System.out.println("Updated existing admin user to default password and role: " + email);
        }
        assertTrue(userRepository.existsByEmail(email));
    }
}
