package com.example.SportsTracker.football.controller;

import com.example.SportsTracker.football.model.FootballStandings;
import com.example.SportsTracker.football.service.FootballStandingsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/football/standings")
@RequiredArgsConstructor
public class FootballStandingsController {

    private final FootballStandingsService service;

    // ── PUBLIC: anyone can view standings ──
    @GetMapping("/{leagueId}")
    public ResponseEntity<List<FootballStandings>> getStandings(@PathVariable String leagueId) {
        return ResponseEntity.ok(service.getStandings(leagueId));
    }

    @GetMapping("/{leagueId}/export")
    public void exportCsv(
            @PathVariable String leagueId,
            @RequestParam(defaultValue = "csv") String format,
            HttpServletResponse response) throws IOException {
        if ("csv".equalsIgnoreCase(format)) {
            service.exportStandingsToCsv(leagueId, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported format. Use format=csv");
        }
    }

    // ── ADMIN ONLY ──
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FootballStandings> create(@RequestBody FootballStandings standing) {
        return ResponseEntity.ok(service.save(standing));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FootballStandings> update(
            @PathVariable String id,
            @RequestBody FootballStandings standing) {
        standing.setId(id);
        return ResponseEntity.ok(service.save(standing));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/league/{leagueId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteByLeague(@PathVariable String leagueId) {
        service.deleteByLeague(leagueId);
        return ResponseEntity.ok().build();
    }
}