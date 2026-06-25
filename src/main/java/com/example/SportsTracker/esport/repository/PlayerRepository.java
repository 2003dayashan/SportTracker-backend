package com.example.SportsTracker.esport.repository;

import com.example.SportsTracker.esport.model.Player;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PlayerRepository extends MongoRepository<Player, String> {
    List<Player> findByTeamId(String teamId);
    List<Player> findByUsernameContainingIgnoreCase(String username);
}