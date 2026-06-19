package com.example.SportsTracker.questboard.dto;

import com.example.SportsTracker.questboard.model.QuestCategory;
import com.example.SportsTracker.questboard.model.QuestStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuestRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category is required")
    private QuestCategory category;

    @Min(value = 1, message = "Points reward must be at least 1")
    private int pointsReward;

    @NotNull(message = "Status is required")
    private QuestStatus status;

    private LocalDateTime expiresAt;
}