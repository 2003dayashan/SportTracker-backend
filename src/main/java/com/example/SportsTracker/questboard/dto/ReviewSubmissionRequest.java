package com.example.SportsTracker.questboard.dto;

import com.example.SportsTracker.questboard.model.SubmissionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewSubmissionRequest {
    @NotNull(message = "Status must be provided")
    private SubmissionStatus status;
}