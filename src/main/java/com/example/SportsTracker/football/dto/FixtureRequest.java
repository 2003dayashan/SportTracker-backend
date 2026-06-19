package com.example.SportsTracker.football.dto;

import com.example.SportsTracker.football.model.FixtureStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FixtureRequest {
    @NotBlank(message = "League ID is required")
    private String leagueId;
    @NotBlank(message = "Home Club ID is required")
    private String homeClubId;
    @NotBlank(message = "Away Club ID is required")
    private String awayClubId;
    @NotNull(message = "Status is required")
    private FixtureStatus status;
    private LocalDateTime kickoffAt;
    private Integer matchday;
}