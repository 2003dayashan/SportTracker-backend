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
@Document(collection = "football_leagues")
public class FootballLeague {
    @Id
    private String id;
    private String name;
    private String country;
    private String season;
    private String logoUrl;
    private boolean active;
    private String apiCode; // e.g., "PL" for Premier League

    @CreatedDate
    private LocalDateTime createdAt;
}