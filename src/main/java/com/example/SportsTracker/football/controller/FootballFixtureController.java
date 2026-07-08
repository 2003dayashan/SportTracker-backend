package com.example.SportsTracker.football.controller;

import com.example.SportsTracker.football.dto.FixtureRequest;
import com.example.SportsTracker.football.dto.ScoreUpdateRequest;
import com.example.SportsTracker.football.model.FootballClub;
import com.example.SportsTracker.football.model.FootballFixture;
import com.example.SportsTracker.football.model.FootballLeague;
import com.example.SportsTracker.football.repository.FootballClubRepository;
import com.example.SportsTracker.football.repository.FootballLeagueRepository;
import com.example.SportsTracker.football.service.FootballFixtureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/football/fixtures")
@RequiredArgsConstructor
public class FootballFixtureController {

    private final FootballFixtureService service;
    private final FootballClubRepository clubRepository;
    private final FootballLeagueRepository leagueRepository;

    // ── PUBLIC: anyone can view fixtures ──
    @GetMapping
    public ResponseEntity<List<FootballFixture>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable String id) {
        FootballFixture fixture = service.getById(id);

        String homeClubName = clubRepository.findById(fixture.getHomeClubId())
                .map(FootballClub::getName).orElse("Unknown");
        String awayClubName = clubRepository.findById(fixture.getAwayClubId())
                .map(FootballClub::getName).orElse("Unknown");
        String leagueName = leagueRepository.findById(fixture.getLeagueId())
                .map(FootballLeague::getName).orElse("Unknown");

        Map<String, Object> enriched = Map.ofEntries(
                Map.entry("id",           fixture.getId()),
                Map.entry("leagueId",     fixture.getLeagueId()),
                Map.entry("leagueName",   leagueName),
                Map.entry("homeClubId",   fixture.getHomeClubId()),
                Map.entry("awayClubId",   fixture.getAwayClubId()),
                Map.entry("homeClubName", homeClubName),
                Map.entry("awayClubName", awayClubName),
                Map.entry("homeScore",    fixture.getHomeScore() != null ? fixture.getHomeScore() : 0),
                Map.entry("awayScore",    fixture.getAwayScore() != null ? fixture.getAwayScore() : 0),
                Map.entry("status",       fixture.getStatus()),
                Map.entry("kickoffAt",    fixture.getKickoffAt() != null ? fixture.getKickoffAt().toString() : ""),
                Map.entry("matchday",     fixture.getMatchday() != null ? fixture.getMatchday() : 1)
        );

        return ResponseEntity.ok(enriched);
    }

    // ── ADMIN ONLY ──
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FootballFixture> create(@Valid @RequestBody FixtureRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FootballFixture> update(
            @PathVariable String id,
            @Valid @RequestBody FixtureRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @PutMapping("/{id}/score")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FootballFixture> updateScore(
            @PathVariable String id,
            @Valid @RequestBody ScoreUpdateRequest req) {
        return ResponseEntity.ok(service.updateScore(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}