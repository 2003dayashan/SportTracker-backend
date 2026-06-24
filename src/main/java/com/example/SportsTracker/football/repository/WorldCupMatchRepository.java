package com.example.SportsTracker.football.repository;

import com.example.SportsTracker.football.model.WorldCupMatch;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface WorldCupMatchRepository
        extends MongoRepository<WorldCupMatch, String> {
    Optional<WorldCupMatch> findByExternalMatchId(Long externalMatchId);
    List<WorldCupMatch> findByStatusOrderByKickoffAtAsc(String status);
    List<WorldCupMatch> findAllByOrderByKickoffAtDesc();
}