package com.example.SportsTracker.questboard.repository;

import com.example.SportsTracker.questboard.model.QuestSubmission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestSubmissionRepository extends MongoRepository<QuestSubmission, String> {
    Optional<QuestSubmission> findByQuestIdAndUserId(String questId, String userId);
}