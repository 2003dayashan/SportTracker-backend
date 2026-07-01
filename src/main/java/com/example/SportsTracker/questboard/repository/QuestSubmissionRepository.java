package com.example.SportsTracker.questboard.repository;

import com.example.SportsTracker.questboard.model.QuestSubmission;
import com.example.SportsTracker.questboard.model.SubmissionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface QuestSubmissionRepository extends MongoRepository<QuestSubmission, String> {
    List<QuestSubmission> findByUserId(String userId);
    List<QuestSubmission> findByUserIdAndStatus(String userId, SubmissionStatus status);
    List<QuestSubmission> findByStatus(SubmissionStatus status);
    Optional<QuestSubmission> findByUserIdAndQuestId(String userId, String questId);
}
