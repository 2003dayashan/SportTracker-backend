package com.example.SportsTracker.questboard.controller;

import com.example.SportsTracker.questboard.model.User;
import com.example.SportsTracker.questboard.security.JwtUtil;
import com.example.SportsTracker.questboard.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController("questboardAuthController")
@RequestMapping("/api/questboard/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    /* DTOs as Java records */
    public record RegisterRequest(String username, String email, String password) {}
    public record LoginRequest(String email, String password) {}
    public record AuthResponse(String id, String username, String email, String role, String token) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req, HttpServletResponse response) {
        User user = authService.register(req.username, req.email, req.password);
        String token = jwtUtil.generateToken(user.getId(), user.getRole().name());
        addCookie(response, token);
        AuthResponse authResp = new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name(), token);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResp);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletResponse response) {
        String token = authService.login(req.email, req.password);
        // Need to fetch user to return details (could also decode token)
        User user = authService.findByEmail(req.email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        }
        addCookie(response, token);
        AuthResponse authResp = new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name(), token);
        return ResponseEntity.ok(authResp);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Clear cookie
        Cookie cookie = new Cookie("quest_token", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof String userId) {
            User user = authService.findById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            AuthResponse resp = new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole().name(), null);
            return ResponseEntity.ok(resp);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }



    private void addCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("quest_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        // Set max age to 1 hour (adjust as needed)
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);
    }
}