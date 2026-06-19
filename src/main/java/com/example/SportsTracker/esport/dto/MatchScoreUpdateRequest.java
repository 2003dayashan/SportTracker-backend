package com.example.SportsTracker.esport.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class MatchScoreUpdateRequest {
    @Min(0)
    private int scoreA;

    @Min(0)
    private int scoreB;
}