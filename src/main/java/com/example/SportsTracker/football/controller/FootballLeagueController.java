package com.example.SportsTracker.football.controller;

import com.example.SportsTracker.football.dto.LeagueRequest;
import com.example.SportsTracker.football.model.FootballLeague;
import com.example.SportsTracker.football.service.FootballApiService;
import com.example.SportsTracker.football.service.FootballLeagueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/football/leagues")
@RequiredArgsConstructor
public class FootballLeagueController {
    private final FootballLeagueService service;
    private final FootballApiService apiService;

    @GetMapping
    public ResponseEntity<List<FootballLeague>> getAll() { return ResponseEntity.ok(service.getAll()); }

    @PostMapping
    public ResponseEntity<FootballLeague> create(@Valid @RequestBody LeagueRequest req) { return ResponseEntity.ok(service.create(req)); }

    @PutMapping("/{id}")
    public ResponseEntity<FootballLeague> update(@PathVariable String id, @Valid @RequestBody LeagueRequest req) { return ResponseEntity.ok(service.update(id, req)); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) { service.delete(id); return ResponseEntity.ok().build(); }

    @PostMapping("/sync/{code}")
    public ResponseEntity<FootballLeague> syncFromApi(@PathVariable String code) { return ResponseEntity.ok(apiService.syncLeague(code)); }
}