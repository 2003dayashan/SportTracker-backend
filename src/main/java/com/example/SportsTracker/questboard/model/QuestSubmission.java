package com.example.SportsTracker.questboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "quest_submissions")
public class QuestSubmission {
    @Id
    private String id;
    private String questId;
    private String userId;
    private SubmissionStatus status;
    private String proofUrl;

    @CreatedDate
    private LocalDateTime submittedAt;

    private LocalDateTime reviewedAt;
    private String reviewedByUserId;
}