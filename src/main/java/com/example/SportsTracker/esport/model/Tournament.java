package com.example.SportsTracker.esport.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tournaments")
public class Tournament {
    @Id
    private String id;
    private String name;
    private String game;
    private TournamentFormat format;
    private TournamentStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int maxTeams;
    private List<String> teamIds;
    private String organizerId;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}