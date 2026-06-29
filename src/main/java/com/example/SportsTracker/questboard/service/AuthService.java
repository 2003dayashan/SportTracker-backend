package com.example.SportsTracker.questboard.service;

import com.example.SportsTracker.exception.DuplicateResourcesException;
import com.example.SportsTracker.exception.UnauthorizedException;
import com.example.SportsTracker.questboard.model.User;
import com.example.SportsTracker.questboard.repository.UserRepository;
import com.example.SportsTracker.questboard.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("questAuthService")
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public User register(String username, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourcesException("Email is already in use");
        }
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourcesException("Username is already in use");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(userRepository.count() == 0 ? User.Role.ADMIN : User.Role.USER);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        return userRepository.save(user);
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        return jwtUtil.generateToken(user.getId(), user.getRole().name());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findById(String id) {
        return userRepository.findById(id).orElse(null);
    }
}