package com.example.SportsTracker.football.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("worldcup_matches")
public class WorldCupMatch {
    @Id
    private String id;
    private Long externalMatchId;
    private String groupName;
    private String stage;
    private String homeTeam;
    private String homeTeamCrestUrl;
    private String awayTeam;
    private String awayTeamCrestUrl;
    private Integer homeScore;
    private Integer awayScore;
    private String status;
    private LocalDateTime kickoffAt;
    private LocalDateTime lastSyncedAt;
}