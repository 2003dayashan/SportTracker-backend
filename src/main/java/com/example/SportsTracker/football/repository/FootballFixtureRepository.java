package com.example.SportsTracker.football.repository;

import com.example.SportsTracker.football.model.FootballFixture;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FootballFixtureRepository extends MongoRepository<FootballFixture, String> {
    List<FootballFixture> findByLeagueId(String leagueId);
    List<FootballFixture> findByLeagueIdAndStatus(String leagueId, com.example.SportsTracker.football.model.FixtureStatus status);
    boolean existsByStatus(com.example.SportsTracker.football.model.FixtureStatus status);
}