package com.example.SportsTracker.questboard.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "quest_submissions")
public class QuestSubmission {
    @Id
    private String id;
    
    private String userId;
    private String username;
    
    private String questId;
    private String questTitle;
    private int points; // Points snapshot in case quest changes
    
    private SubmissionStatus status;
    private LocalDateTime timestamp;
}
