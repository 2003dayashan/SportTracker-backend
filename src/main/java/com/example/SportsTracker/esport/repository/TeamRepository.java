package com.example.SportsTracker.esport.repository;

import com.example.SportsTracker.esport.model.Team;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends MongoRepository<Team, String> {
    List<Team> findByNameContainingIgnoreCase(String name);
    List<Team> findByTournamentId(String tournamentId);
}