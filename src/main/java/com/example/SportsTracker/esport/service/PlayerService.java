package com.example.SportsTracker.esport.service;

import com.example.SportsTracker.esport.dto.PlayerRequest;
import com.example.SportsTracker.esport.model.Player;
import com.example.SportsTracker.esport.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository repository;

    public List<Player> getAllPlayers(String teamId) {
        if (teamId != null) return repository.findByTeamId(teamId);
        return repository.findAll();
    }

    public Player getPlayer(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found: " + id));
    }

    public Player createPlayer(PlayerRequest request, String userId) {
        Player player = Player.builder()
                .userId(userId)
                .teamId(request.getTeamId())
                .username(request.getUsername())
                .role(request.getRole())
                .build();
        return repository.save(player);
    }

    public Player updatePlayer(String id, PlayerRequest request) {
        Player player = getPlayer(id);
        player.setUsername(request.getUsername());
        player.setTeamId(request.getTeamId());
        player.setRole(request.getRole());
        return repository.save(player);
    }

    public void deletePlayer(String id) {
        repository.deleteById(id);
    }

    public List<Player> searchPlayers(String query) {
        return repository.findByUsernameContainingIgnoreCase(query);
    }
}