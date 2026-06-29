package com.example.SportsTracker.esport.controller;

import com.example.SportsTracker.esport.dto.MatchScoreUpdateRequest;
import com.example.SportsTracker.esport.model.Match;
import com.example.SportsTracker.esport.model.MatchStatus;
import com.example.SportsTracker.esport.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService service;

    @PostMapping
    public ResponseEntity<Match> create(@RequestBody Match match) {
        return ResponseEntity.ok(service.createMatch(match));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Match> getMatch(@PathVariable String id) {
        return ResponseEntity.ok(service.getMatch(id));
    }

    @PutMapping("/{id}/score")
    public ResponseEntity<Match> updateScore(@PathVariable String id, @Valid @RequestBody MatchScoreUpdateRequest request) {
        return ResponseEntity.ok(service.updateScore(id, request));
    }


}