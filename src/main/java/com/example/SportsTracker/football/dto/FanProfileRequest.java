package com.example.SportsTracker.football.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FanProfileRequest {
    @NotBlank(message = "Display name is required")
    private String displayName;

    @NotBlank(message = "Country code is required")
    private String countryCode;
}