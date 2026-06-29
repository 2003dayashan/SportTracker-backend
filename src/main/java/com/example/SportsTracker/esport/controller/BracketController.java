package com.example.SportsTracker.esport.controller;

import com.example.SportsTracker.esport.model.Match;
import com.example.SportsTracker.esport.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tournaments/{tournamentId}/bracket")
@RequiredArgsConstructor
public class BracketController {

    private final MatchRepository matchRepository;

    @GetMapping
    public ResponseEntity<List<Match>> getBracket(@PathVariable String tournamentId) {
        List<Match> matches = matchRepository.findByTournamentId(tournamentId)
                .stream()
                .sorted(Comparator.comparing(Match::getScheduledAt))
                .collect(Collectors.toList());
        return ResponseEntity.ok(matches);
    }
}