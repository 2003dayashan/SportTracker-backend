package com.example.SportsTracker.football.repository;

import com.example.SportsTracker.football.model.WorldCupStanding;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WorldCupStandingRepository
        extends MongoRepository<WorldCupStanding, String> {
    List<WorldCupStanding> findByGroupNameOrderByPositionAsc(String groupName);
    List<WorldCupStanding> findAllByOrderByGroupNameAscPositionAsc();
}