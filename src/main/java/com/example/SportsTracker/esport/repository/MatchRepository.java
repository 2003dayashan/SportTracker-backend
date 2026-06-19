package com.example.SportsTracker.esport.repository;

import com.example.SportsTracker.esport.model.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends MongoRepository<Match, String> {
    Page<Match> findByTournamentId(String tournamentId, Pageable pageable);
    List<Match> findByTeamAIdOrTeamBId(String teamAId, String teamBId);
}