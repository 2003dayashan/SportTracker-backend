package com.example.SportsTracker.questboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "leaderboards")
public class Leaderboard {
    @Id
    private String id;
    private String userId;
    private String username;
    private int totalPoints;
    private int rank;
    private int questsCompleted;

    @LastModifiedDate
    private LocalDateTime lastUpdated;
}