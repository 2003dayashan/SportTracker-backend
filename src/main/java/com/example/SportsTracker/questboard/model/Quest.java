package com.example.SportsTracker.questboard.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "quests")
public class Quest {
    @Id
    private String id;
    
    private String title;
    private String description;
    private ServiceType serviceType;
    private int points;
    private boolean isCompleted;
}
