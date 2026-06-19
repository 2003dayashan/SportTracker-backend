package com.example.SportsTracker.esport.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TeamRequest {
    @NotBlank(message = "Team name is required")
    private String name;
    private String tournamentId;
    private String logoUrl;
}