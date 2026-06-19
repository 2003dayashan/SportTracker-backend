package com.example.SportsTracker.football.repository;

import com.example.SportsTracker.football.model.FootballLeague;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FootballLeagueRepository extends MongoRepository<FootballLeague, String> {
    Optional<FootballLeague> findByApiCode(String apiCode);
}