package com.example.SportsTracker.core.controller;

import com.example.SportsTracker.core.model.Role;
import com.example.SportsTracker.core.model.User;
import com.example.SportsTracker.core.repository.UserRepository;
import com.example.SportsTracker.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'GUILD_MASTER')")
public class AdminUserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<Page<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> users = userRepository.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already in use");
        }

        List<Role> roles = request.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = List.of(Role.ROLE_USER);
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        
        User responseUser = User.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .roles(savedUser.getRoles())
                .enabled(savedUser.isEnabled())
                .createdAt(savedUser.getCreatedAt())
                .updatedAt(savedUser.getUpdatedAt())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id, HttpSession session) {
        String authenticatedUserId = getAuthenticatedUserId(session);

        if (id.equals(authenticatedUserId)) {
            throw new IllegalArgumentException("Self-deletion is not allowed");
        }

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }

        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<User> updateRoles(
            @PathVariable String id,
            @Valid @RequestBody UpdateRolesRequest request,
            HttpSession session) {

        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            throw new IllegalArgumentException("Roles list cannot be empty");
        }

        String authenticatedUserId = getAuthenticatedUserId(session);

        if (id.equals(authenticatedUserId)) {
            boolean remainsAdmin = request.getRoles().contains(Role.ROLE_ADMIN) 
                    || request.getRoles().contains(Role.ROLE_GUILD_MASTER);
            if (!remainsAdmin) {
                throw new IllegalArgumentException("Self-demotion is not allowed");
            }
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRoles(request.getRoles());
        User updatedUser = userRepository.save(user);
        
        User responseUser = User.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .email(updatedUser.getEmail())
                .roles(updatedUser.getRoles())
                .enabled(updatedUser.isEnabled())
                .createdAt(updatedUser.getCreatedAt())
                .updatedAt(updatedUser.getUpdatedAt())
                .build();

        return ResponseEntity.ok(responseUser);
    }

    private String getAuthenticatedUserId(HttpSession session) {
        String authenticatedUserId = null;
        if (session != null) {
            authenticatedUserId = (String) session.getAttribute("USER_ID");
        }
        if (authenticatedUserId == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                authenticatedUserId = auth.getName();
            }
        }
        return authenticatedUserId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateUserRequest {
        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        private List<Role> roles;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRolesRequest {
        private List<Role> roles;
    }
}
