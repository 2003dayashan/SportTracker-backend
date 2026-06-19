package com.example.SportsTracker.football.repository;

import com.example.SportsTracker.football.model.FootballStandings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FootballStandingsRepository extends MongoRepository<FootballStandings, String> {
    List<FootballStandings> findByLeagueIdOrderByPositionAsc(String leagueId);
    Optional<FootballStandings> findByLeagueIdAndClubId(String leagueId, String clubId);
    void deleteByLeagueId(String leagueId);
}