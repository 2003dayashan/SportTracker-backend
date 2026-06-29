package com.example.SportsTracker.esport.controller;

import com.example.SportsTracker.esport.dto.TeamRequest;
import com.example.SportsTracker.esport.model.Team;
import com.example.SportsTracker.esport.service.TeamService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService service;

    @GetMapping
    public ResponseEntity<List<Team>> getAll() {
        return ResponseEntity.ok(service.getAllTeams());
    }

    @PostMapping
    public ResponseEntity<Team> create(@Valid @RequestBody TeamRequest request, HttpSession session) {
        String userId = (String) session.getAttribute("USER_ID");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createTeam(request, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getOne(@PathVariable String id) {
        return ResponseEntity.ok(service.getTeam(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Team>> search(@RequestParam String q) {
        return ResponseEntity.ok(service.searchTeams(q));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Team> update(@PathVariable String id, @Valid @RequestBody TeamRequest request) {
        return ResponseEntity.ok(service.updateTeam(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }
}