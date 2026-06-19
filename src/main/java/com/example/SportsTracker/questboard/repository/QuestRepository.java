package com.example.SportsTracker.questboard.repository;

import com.example.SportsTracker.questboard.model.Quest;
import com.example.SportsTracker.questboard.model.QuestCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestRepository extends MongoRepository<Quest, String> {
    Page<Quest> findByCategory(QuestCategory category, Pageable pageable);
}