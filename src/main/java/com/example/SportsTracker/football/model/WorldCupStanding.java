package com.example.SportsTracker.football.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("worldcup_standings")
public class WorldCupStanding {
    @Id
    private String id;
    private String groupName;
    private int position;
    private String teamName;
    private String teamCrestUrl;
    private int playedGames;
    private int won;
    private int draw;
    private int lost;
    private int points;
    private int goalsFor;
    private int goalsAgainst;
    private int goalDifference;
    private LocalDateTime lastSyncedAt;
}