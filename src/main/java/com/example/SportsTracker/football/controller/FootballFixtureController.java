package com.example.SportsTracker.football.controller;

import com.example.SportsTracker.football.dto.FixtureRequest;
import com.example.SportsTracker.football.dto.ScoreUpdateRequest;
import com.example.SportsTracker.football.model.FootballFixture;
import com.example.SportsTracker.football.service.FootballFixtureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/football/fixtures")
@RequiredArgsConstructor
public class FootballFixtureController {
    private final FootballFixtureService service;

    @GetMapping
    public ResponseEntity<List<FootballFixture>> getAll() { return ResponseEntity.ok(service.getAll()); }

    @PostMapping
    public ResponseEntity<FootballFixture> create(@Valid @RequestBody FixtureRequest req) { return ResponseEntity.ok(service.create(req)); }

    @PutMapping("/{id}")
    public ResponseEntity<FootballFixture> update(@PathVariable String id, @Valid @RequestBody FixtureRequest req) { return ResponseEntity.ok(service.update(id, req)); }

    @PutMapping("/{id}/score")
    public ResponseEntity<FootballFixture> updateScore(@PathVariable String id, @Valid @RequestBody ScoreUpdateRequest req) { return ResponseEntity.ok(service.updateScore(id, req)); }
}