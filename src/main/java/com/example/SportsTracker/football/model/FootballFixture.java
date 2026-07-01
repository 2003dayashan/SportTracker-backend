package com.example.SportsTracker.football.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "football_fixtures")
public class FootballFixture {
    @Id
    private String id;
    private String leagueId;
    private String homeClubId;
    private String awayClubId;
    private Integer homeScore;
    private Integer awayScore;
    private FixtureStatus status;
    private LocalDateTime kickoffAt;
    private Integer matchday;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}