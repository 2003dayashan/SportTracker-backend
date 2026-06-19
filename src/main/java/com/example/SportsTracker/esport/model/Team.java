package com.example.SportsTracker.esport.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "teams")
public class Team {
    @Id
    private String id;
    private String name;
    private String tournamentId;
    private List<String> playerIds;
    private String captainUserId;
    private String logoUrl;

    @CreatedDate
    private LocalDateTime createdAt;
}