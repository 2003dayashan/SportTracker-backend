package com.example.SportsTracker.football.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LeagueRequest {
    @NotBlank(message = "Name is required")
    private String name;
    private String country;
    private String season;
    private String logoUrl;
    private boolean active;
    private String apiCode;
}