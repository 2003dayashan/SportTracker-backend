package com.example.SportsTracker.football.controller;

import com.example.SportsTracker.football.model.WorldCupMatch;
import com.example.SportsTracker.football.model.WorldCupStanding;
import com.example.SportsTracker.football.repository.WorldCupMatchRepository;
import com.example.SportsTracker.football.repository.WorldCupStandingRepository;
import com.example.SportsTracker.football.service.WorldCupSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/football/worldcup")
@RequiredArgsConstructor
public class WorldCupController {

    private final WorldCupStandingRepository standingRepository;
    private final WorldCupMatchRepository matchRepository;
    private final WorldCupSyncService syncService;

    @GetMapping("/standings")
    public ResponseEntity<List<WorldCupStanding>> getStandings() {
        return ResponseEntity.ok(
                standingRepository.findAllByOrderByGroupNameAscPositionAsc());
    }

    @GetMapping("/matches")
    public ResponseEntity<List<WorldCupMatch>> getMatches() {
        return ResponseEntity.ok(
                matchRepository.findAllByOrderByKickoffAtDesc());
    }

    @GetMapping("/matches/live")
    public ResponseEntity<List<WorldCupMatch>> getLiveMatches() {
        return ResponseEntity.ok(
                matchRepository.findByStatusOrderByKickoffAtAsc("IN_PLAY"));
    }

    @PostMapping("/sync")
    public ResponseEntity<String> manualSync() {
        syncService.syncWorldCupData();
        return ResponseEntity.ok("Sync triggered successfully!");
    }
}