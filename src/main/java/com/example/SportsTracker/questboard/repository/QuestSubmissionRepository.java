package com.example.SportsTracker.questboard.repository;

import com.example.SportsTracker.questboard.model.QuestSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface QuestSubmissionRepository extends MongoRepository<QuestSubmission, String> {
    Optional<QuestSubmission> findById(String id);
    List<QuestSubmission> findByQuestId(String questId);
    Page<QuestSubmission> findByQuestId(String questId, Pageable pageable);
    List<QuestSubmission> findByUserId(String userId);
    Page<QuestSubmission> findByUserId(String userId, Pageable pageable);
    List<QuestSubmission> findByStatus(QuestSubmission.Status status);
    Page<QuestSubmission> findByStatus(QuestSubmission.Status status, Pageable pageable);
    List<QuestSubmission> findByQuestIdAndUserId(String questId, String userId);
    Page<QuestSubmission> findByQuestIdAndUserId(String questId, String userId, Pageable pageable);
    List<QuestSubmission> findByQuestIdAndStatus(String questId, QuestSubmission.Status status);
    Page<QuestSubmission> findByQuestIdAndStatus(String questId, QuestSubmission.Status status, Pageable pageable);
}