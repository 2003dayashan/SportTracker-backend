package com.example.SportsTracker.football.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScoreUpdateRequest {
    @NotNull
    @Min(0)
    private Integer homeScore;

    @NotNull
    @Min(0)
    private Integer awayScore;
}