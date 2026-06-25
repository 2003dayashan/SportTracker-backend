package com.example.SportsTracker.esport.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlayerRequest {
    @NotBlank(message = "Username is required")
    private String username;
    private String teamId;
    private String role;
}