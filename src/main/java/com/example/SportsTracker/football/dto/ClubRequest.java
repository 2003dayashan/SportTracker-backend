package com.example.SportsTracker.football.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClubRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "League ID is required")
    private String leagueId;
    private String country;
    private String stadiumName;
    private String badgeUrl;
    private Integer foundedYear;
}