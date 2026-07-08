package com.example.SportsTracker.football.controller;

import com.example.SportsTracker.football.dto.LeagueRequest;
import com.example.SportsTracker.football.model.FootballLeague;
import com.example.SportsTracker.football.service.FootballApiService;
import com.example.SportsTracker.football.service.FootballLeagueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/football/leagues")
@RequiredArgsConstructor
public class FootballLeagueController {

    private final FootballLeagueService service;
    private final FootballApiService apiService;

    // ── PUBLIC ──
    @GetMapping
    public ResponseEntity<List<FootballLeague>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // ── ADMIN ONLY ──
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FootballLeague> create(@Valid @RequestBody LeagueRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FootballLeague> update(
            @PathVariable String id,
            @Valid @RequestBody LeagueRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    // Sync league info only
    @PostMapping("/sync/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FootballLeague> syncFromApi(@PathVariable String code) {
        return ResponseEntity.ok(apiService.syncLeague(code));
    }

    // Sync league + clubs + fixtures + standings එකවර
    @PostMapping("/sync-all/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> syncAll(@PathVariable String code) {
        return ResponseEntity.ok(apiService.syncAll(code));
    }
}