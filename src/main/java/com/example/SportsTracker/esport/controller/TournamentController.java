package com.example.SportsTracker.esport.controller;

import com.example.SportsTracker.esport.dto.TournamentRequest;
import com.example.SportsTracker.esport.model.Tournament;
import com.example.SportsTracker.esport.model.TournamentStatus;
import com.example.SportsTracker.esport.service.TournamentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService service;

    @GetMapping
    public ResponseEntity<Page<Tournament>> getAll(
            @RequestParam(required = false) TournamentStatus status,
            @RequestParam(required = false) String game,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getTournaments(status, game, PageRequest.of(page, size)));
    }

    @PostMapping
    public ResponseEntity<Tournament> create(@Valid @RequestBody TournamentRequest request, HttpSession session) {
        String userId = (String) session.getAttribute("USER_ID");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createTournament(request, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tournament> update(@PathVariable String id, @Valid @RequestBody TournamentRequest request) {
        return ResponseEntity.ok(service.updateTournament(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteTournament(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getOne(@PathVariable String id) {
        return ResponseEntity.ok(service.getTournament(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Tournament>> search(@RequestParam String q) {
        return ResponseEntity.ok(service.searchTournaments(q));
    }
}