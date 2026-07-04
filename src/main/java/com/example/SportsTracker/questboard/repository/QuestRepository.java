package com.example.SportsTracker.questboard.repository;

import com.example.SportsTracker.questboard.model.Quest;
import com.example.SportsTracker.questboard.model.ServiceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface QuestRepository extends MongoRepository<Quest, String> {
    Page<Quest> findByServiceType(ServiceType serviceType, Pageable pageable);
}
