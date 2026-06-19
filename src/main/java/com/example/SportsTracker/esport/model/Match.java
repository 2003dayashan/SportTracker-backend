package com.example.SportsTracker.esport.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "matches")
public class Match {
    @Id
    private String id;
    private String tournamentId;
    private String teamAId;
    private String teamBId;
    private int scoreA;
    private int scoreB;
    private String winnerId;
    private MatchStatus status;
    private LocalDateTime scheduledAt;
    private LocalDateTime completedAt;
}