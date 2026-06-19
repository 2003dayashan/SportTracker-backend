package com.example.SportsTracker.esport.dto;

import com.example.SportsTracker.esport.model.TournamentFormat;
import com.example.SportsTracker.esport.model.TournamentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TournamentRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Game is required")
    private String game;

    @NotNull(message = "Format is required")
    private TournamentFormat format;

    @NotNull(message = "Status is required")
    private TournamentStatus status;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    @Min(value = 2, message = "Maximum teams must be at least 2")
    private int maxTeams;
}