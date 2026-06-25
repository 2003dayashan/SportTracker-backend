package com.example.SportsTracker.esport.repository;

import com.example.SportsTracker.esport.model.Match;
import com.example.SportsTracker.esport.model.MatchStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MatchRepository extends MongoRepository<Match, String> {
    List<Match> findByTournamentId(String tournamentId);
    List<Match> findByStatus(MatchStatus status);
    List<Match> findByTournamentIdAndStatus(String tournamentId, MatchStatus status);
}