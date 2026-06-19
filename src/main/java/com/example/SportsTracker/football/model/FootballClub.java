package com.example.SportsTracker.football.model;

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
@Document(collection = "football_clubs")
public class FootballClub {
    @Id
    private String id;
    private String name;
    private String leagueId;
    private String country;
    private String stadiumName;
    private String badgeUrl;
    private Integer foundedYear;

    @CreatedDate
    private LocalDateTime createdAt;
}