package com.example.SportsTracker.football.controller;

import com.example.SportsTracker.football.model.FootballStandings;
import com.example.SportsTracker.football.service.FootballStandingsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/football/standings")
@RequiredArgsConstructor
public class FootballStandingsController {
    private final FootballStandingsService service;

    @GetMapping("/{leagueId}")
    public ResponseEntity<List<FootballStandings>> getStandings(@PathVariable String leagueId) {
        return ResponseEntity.ok(service.getStandings(leagueId));
    }

    @GetMapping("/{leagueId}/export")
    public void exportCsv(@PathVariable String leagueId, @RequestParam(defaultValue = "csv") String format, HttpServletResponse response) throws IOException {
        if ("csv".equalsIgnoreCase(format)) {
            service.exportStandingsToCsv(leagueId, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported format. Use format=csv");
        }
    }
}