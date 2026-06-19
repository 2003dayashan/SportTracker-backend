package com.example.SportsTracker.core.service;

import com.example.SportsTracker.core.dto.AuthResponse;
import com.example.SportsTracker.core.dto.SigninRequest;
import com.example.SportsTracker.core.dto.SignupRequest;
import com.example.SportsTracker.core.model.Role;
import com.example.SportsTracker.core.model.User;
import com.example.SportsTracker.core.repository.UserRepository;
import com.example.SportsTracker.exception.DuplicateResourcesException;
import com.example.SportsTracker.exception.UnauthorizedException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourcesException("Email is already in use");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourcesException("Username is already in use");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of(Role.ROLE_USER))
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        return mapToAuthResponse(savedUser);
    }

    public AuthResponse signin(SigninRequest request, HttpSession session) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        // Store standard context in session
        session.setAttribute("USER_ID", user.getId());
        session.setAttribute("USER_ROLES", user.getRoles());

        return mapToAuthResponse(user);
    }

    public void signout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    private AuthResponse mapToAuthResponse(User user) {
        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }
}