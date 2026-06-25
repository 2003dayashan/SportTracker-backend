package com.example.SportsTracker.esport.repository;

import com.example.SportsTracker.esport.model.Tournament;
import com.example.SportsTracker.esport.model.TournamentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TournamentRepository extends MongoRepository<Tournament, String> {
    Page<Tournament> findByStatus(TournamentStatus status, Pageable pageable);
    Page<Tournament> findByGame(String game, Pageable pageable);
    Page<Tournament> findByStatusAndGame(TournamentStatus status, String game, Pageable pageable);
    List<Tournament> findByStatusAndEndDateBefore(TournamentStatus status, LocalDateTime date);
    List<Tournament> findByNameContainingIgnoreCase(String name);
}