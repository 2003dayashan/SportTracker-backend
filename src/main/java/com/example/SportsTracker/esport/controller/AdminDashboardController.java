package com.example.SportsTracker.esport.controller;

import com.example.SportsTracker.esport.repository.MatchRepository;
import com.example.SportsTracker.esport.repository.PlayerRepository;
import com.example.SportsTracker.esport.repository.TeamRepository;
import com.example.SportsTracker.esport.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;

    @GetMapping
    public ResponseEntity<Map<String, Long>> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("tournaments", tournamentRepository.count());
        stats.put("teams", teamRepository.count());
        stats.put("players", playerRepository.count());
        stats.put("matches", matchRepository.count());
        return ResponseEntity.ok(stats);
    }
}