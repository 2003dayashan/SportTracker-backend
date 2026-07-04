package com.example.SportsTracker.core.controller;

import com.example.SportsTracker.core.model.Role;
import com.example.SportsTracker.core.model.User;
import com.example.SportsTracker.core.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean(name = "coreUserRepository")
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User adminUser;
    private User regularUser;
    private MockHttpSession adminSession;
    private MockHttpSession userSession;

    @BeforeEach
    void setUp() {
        adminUser = User.builder()
                .id("admin123")
                .username("admin")
                .email("admin@test.com")
                .roles(List.of(Role.ROLE_ADMIN))
                .enabled(true)
                .build();

        regularUser = User.builder()
                .id("user456")
                .username("user")
                .email("user@test.com")
                .roles(List.of(Role.ROLE_USER))
                .enabled(true)
                .build();

        adminSession = new MockHttpSession();
        adminSession.setAttribute("USER_ID", "admin123");
        adminSession.setAttribute("USER_ROLES", List.of(Role.ROLE_ADMIN));

        userSession = new MockHttpSession();
        userSession.setAttribute("USER_ID", "user456");
        userSession.setAttribute("USER_ROLES", List.of(Role.ROLE_USER));
    }

    @Test
    @WithMockUser(username = "admin123", roles = {"ADMIN"})
    void getUsers_success() throws Exception {
        List<User> userList = List.of(adminUser, regularUser);
        Mockito.when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(userList));

        mockMvc.perform(get("/api/admin/users")
                        .session(adminSession)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "username")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("admin"))
                .andExpect(jsonPath("$.content[1].username").value("user"));
    }

    @Test
    void getUsers_unauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user456", roles = {"USER"})
    void getUsers_forbidden() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .session(userSession))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin123", roles = {"ADMIN"})
    void createUser_success() throws Exception {
        AdminUserController.CreateUserRequest request = AdminUserController.CreateUserRequest.builder()
                .username("newUser")
                .email("new@test.com")
                .password("password123")
                .roles(List.of(Role.ROLE_USER))
                .build();

        User savedUser = User.builder()
                .id("new789")
                .username("newUser")
                .email("new@test.com")
                .roles(List.of(Role.ROLE_USER))
                .enabled(true)
                .build();

        Mockito.when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        Mockito.when(userRepository.existsByUsername("newUser")).thenReturn(false);
        Mockito.when(userRepository.save(any(User.class))).thenReturn(savedUser);

        mockMvc.perform(post("/api/admin/users")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newUser"))
                .andExpect(jsonPath("$.email").value("new@test.com"));
    }

    @Test
    @WithMockUser(username = "admin123", roles = {"ADMIN"})
    void createUser_duplicateEmail_badRequest() throws Exception {
        AdminUserController.CreateUserRequest request = AdminUserController.CreateUserRequest.builder()
                .username("newUser")
                .email("existing@test.com")
                .password("password123")
                .roles(List.of(Role.ROLE_USER))
                .build();

        Mockito.when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        mockMvc.perform(post("/api/admin/users")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email is already in use"));
    }

    @Test
    @WithMockUser(username = "admin123", roles = {"ADMIN"})
    void deleteUser_success() throws Exception {
        Mockito.when(userRepository.existsById("user456")).thenReturn(true);

        mockMvc.perform(delete("/api/admin/users/user456")
                        .session(adminSession))
                .andExpect(status().isNoContent());

        Mockito.verify(userRepository, Mockito.times(1)).deleteById("user456");
    }

    @Test
    @WithMockUser(username = "admin123", roles = {"ADMIN"})
    void deleteUser_selfDeletion_badRequest() throws Exception {
        mockMvc.perform(delete("/api/admin/users/admin123")
                        .session(adminSession))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Self-deletion is not allowed"));
    }

    @Test
    @WithMockUser(username = "admin123", roles = {"ADMIN"})
    void deleteUser_notFound() throws Exception {
        Mockito.when(userRepository.existsById("nonexistent")).thenReturn(false);

        mockMvc.perform(delete("/api/admin/users/nonexistent")
                        .session(adminSession))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    @WithMockUser(username = "admin123", roles = {"ADMIN"})
    void updateRoles_success() throws Exception {
        AdminUserController.UpdateRolesRequest request = new AdminUserController.UpdateRolesRequest(
                List.of(Role.ROLE_USER, Role.ROLE_ADMIN)
        );

        User updatedUser = User.builder()
                .id("user456")
                .username("user")
                .email("user@test.com")
                .roles(List.of(Role.ROLE_USER, Role.ROLE_ADMIN))
                .enabled(true)
                .build();

        Mockito.when(userRepository.findById("user456")).thenReturn(Optional.of(regularUser));
        Mockito.when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/admin/users/user456/roles")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"))
                .andExpect(jsonPath("$.roles[1]").value("ROLE_ADMIN"));
    }

    @Test
    @WithMockUser(username = "admin123", roles = {"ADMIN"})
    void updateRoles_selfDemotion_badRequest() throws Exception {
        // Demoting self to only ROLE_USER
        AdminUserController.UpdateRolesRequest request = new AdminUserController.UpdateRolesRequest(
                List.of(Role.ROLE_USER)
        );

        mockMvc.perform(put("/api/admin/users/admin123/roles")
                        .session(adminSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Self-demotion is not allowed"));
    }
}
