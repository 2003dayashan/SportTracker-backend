package com.example.SportsTracker.football.controller;

import com.example.SportsTracker.football.dto.ClubRequest;
import com.example.SportsTracker.football.model.FootballClub;
import com.example.SportsTracker.football.service.FootballClubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/football/clubs")
@RequiredArgsConstructor
public class FootballClubController {

    private final FootballClubService service;

    // ── PUBLIC: anyone can view clubs ──
    @GetMapping
    public ResponseEntity<List<FootballClub>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // ── ADMIN ONLY ──
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FootballClub> create(@Valid @RequestBody ClubRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FootballClub> update(
            @PathVariable String id,
            @Valid @RequestBody ClubRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}