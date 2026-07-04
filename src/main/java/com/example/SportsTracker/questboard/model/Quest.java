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
    
    private int difficulty; // 1-5 rating
    private boolean isLiveEventRelated;
    private String relatedMatchId;
}
