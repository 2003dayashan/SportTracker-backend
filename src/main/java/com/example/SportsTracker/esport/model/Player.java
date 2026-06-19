package com.example.SportsTracker.esport.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "players")
public class Player {
    @Id
    private String id;
    private String userId;
    private String teamId;
    private String username;
    private String role;
    private Map<String, Object> stats;
}