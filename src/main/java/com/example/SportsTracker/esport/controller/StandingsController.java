package com.example.SportsTracker.esport.controller;

import com.example.SportsTracker.esport.dto.StandingDto;
import com.example.SportsTracker.esport.model.Match;
import com.example.SportsTracker.esport.model.MatchStatus;
import com.example.SportsTracker.esport.model.Team;
import com.example.SportsTracker.esport.repository.MatchRepository;
import com.example.SportsTracker.esport.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tournaments/{tournamentId}/standings")
@RequiredArgsConstructor
public class StandingsController {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;

    @GetMapping
    public ResponseEntity<List<StandingDto>> getStandings(@PathVariable String tournamentId) {
        List<Match> matches = matchRepository.findByTournamentIdAndStatus(tournamentId, MatchStatus.COMPLETED);
        List<Team> teams = teamRepository.findByTournamentId(tournamentId);

        Map<String, StandingDto> table = new HashMap<>();
        for (Team t : teams) {
            table.put(t.getId(), new StandingDto(t.getId(), t.getName(), 0, 0, 0, 0));
        }

        for (Match m : matches) {
            StandingDto a = table.get(m.getTeamAId());
            StandingDto b = table.get(m.getTeamBId());
            if (a == null || b == null) continue;

            a.setPlayed(a.getPlayed() + 1);
            b.setPlayed(b.getPlayed() + 1);

            if (m.getWinnerId() != null && m.getWinnerId().equals(m.getTeamAId())) {
                a.setWins(a.getWins() + 1);
                a.setPoints(a.getPoints() + 3);
                b.setLosses(b.getLosses() + 1);
            } else if (m.getWinnerId() != null && m.getWinnerId().equals(m.getTeamBId())) {
                b.setWins(b.getWins() + 1);
                b.setPoints(b.getPoints() + 3);
                a.setLosses(a.getLosses() + 1);
            }
        }

        List<StandingDto> result = table.values().stream()
                .sorted(Comparator.comparingInt(StandingDto::getPoints).reversed())
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}