package com.example.SportsTracker.esport.service;

import com.example.SportsTracker.esport.dto.TeamRequest;
import com.example.SportsTracker.esport.model.Team;
import com.example.SportsTracker.esport.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository repository;

    public Team createTeam(TeamRequest request, String captainId) {
        Team team = Team.builder()
                .name(request.getName())
                .tournamentId(request.getTournamentId())
                .logoUrl(request.getLogoUrl())
                .captainUserId(captainId)
                .playerIds(new ArrayList<>())
                .build();
        return repository.save(team);
    }

    public List<Team> getAllTeams() {
        return repository.findAll();
    }

    public Team getTeam(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found: " + id));
    }

    public Team updateTeam(String id, TeamRequest request) {
        Team team = getTeam(id);
        team.setName(request.getName());
        team.setTournamentId(request.getTournamentId());
        team.setLogoUrl(request.getLogoUrl());
        return repository.save(team);
    }

    public void deleteTeam(String id) {
        repository.deleteById(id);
    }

    public List<Team> searchTeams(String query) {
        return repository.findByNameContainingIgnoreCase(query);
    }
}