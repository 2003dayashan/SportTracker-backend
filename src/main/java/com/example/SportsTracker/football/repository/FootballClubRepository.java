package com.example.SportsTracker.football.repository;

import com.example.SportsTracker.football.model.FootballClub;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FootballClubRepository extends MongoRepository<FootballClub, String> {
    List<FootballClub> findByLeagueId(String leagueId);
    Optional<FootballClub> findByNameAndLeagueId(String name, String leagueId);
}