package com.example.SportsTracker.questboard.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "quest_submissions")
public class QuestSubmission {
    public enum Status { PENDING, APPROVED, REJECTED }

    @Id
    private String id;
    private String questId; // Reference to Quest
    private String userId; // Reference to User (Adventurer)
    @NotBlank(message = "Proof text is required")
    private String proofText;
    private String proofFileUrl; // nullable
    private Status status;
    private Date submittedAt;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getQuestId() { return questId; }
    public void setQuestId(String questId) { this.questId = questId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getProofText() { return proofText; }
    public void setProofText(String proofText) { this.proofText = proofText; }

    public String getProofFileUrl() { return proofFileUrl; }
    public void setProofFileUrl(String proofFileUrl) { this.proofFileUrl = proofFileUrl; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Date getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Date submittedAt) { this.submittedAt = submittedAt; }
}