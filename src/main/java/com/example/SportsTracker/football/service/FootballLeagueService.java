package com.example.SportsTracker.football.service;

import com.example.SportsTracker.exception.ResourceNotFoundException;
import com.example.SportsTracker.football.dto.LeagueRequest;
import com.example.SportsTracker.football.model.FootballLeague;
import com.example.SportsTracker.football.repository.FootballLeagueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FootballLeagueService {
    private final FootballLeagueRepository repository;

    public List<FootballLeague> getAll() { return repository.findAll(); }

    public FootballLeague create(LeagueRequest req) {
        FootballLeague league = FootballLeague.builder()
                .name(req.getName()).country(req.getCountry())
                .season(req.getSeason()).logoUrl(req.getLogoUrl())
                .apiCode(req.getApiCode()).active(req.isActive()).build();
        return repository.save(league);
    }

    public FootballLeague update(String id, LeagueRequest req) {
        FootballLeague league = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("League not found"));
        league.setName(req.getName()); league.setCountry(req.getCountry());
        league.setSeason(req.getSeason()); league.setLogoUrl(req.getLogoUrl());
        league.setActive(req.isActive()); league.setApiCode(req.getApiCode());
        return repository.save(league);
    }

    public void delete(String id) { repository.deleteById(id); }
}