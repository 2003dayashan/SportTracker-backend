package com.example.SportsTracker.esport.controller;

import com.example.SportsTracker.esport.dto.PlayerRequest;
import com.example.SportsTracker.esport.model.Player;
import com.example.SportsTracker.esport.service.PlayerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService service;

    @GetMapping
    public ResponseEntity<List<Player>> getAll(@RequestParam(required = false) String teamId) {
        return ResponseEntity.ok(service.getAllPlayers(teamId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getOne(@PathVariable String id) {
        return ResponseEntity.ok(service.getPlayer(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Player>> search(@RequestParam String q) {
        return ResponseEntity.ok(service.searchPlayers(q));
    }

    @PostMapping
    public ResponseEntity<Player> create(@Valid @RequestBody PlayerRequest request, HttpSession session) {
        String userId = (String) session.getAttribute("USER_ID");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createPlayer(request, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Player> update(@PathVariable String id, @Valid @RequestBody PlayerRequest request) {
        return ResponseEntity.ok(service.updatePlayer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }
}