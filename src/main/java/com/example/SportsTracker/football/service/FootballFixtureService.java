package com.example.SportsTracker.football.service;

import com.example.SportsTracker.exception.ResourceNotFoundException;
import com.example.SportsTracker.football.dto.FixtureRequest;
import com.example.SportsTracker.football.dto.ScoreUpdateRequest;
import com.example.SportsTracker.football.model.FixtureStatus;
import com.example.SportsTracker.football.model.FootballFixture;
import com.example.SportsTracker.football.repository.FootballFixtureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FootballFixtureService {
    private final FootballFixtureRepository repository;
    private final FootballStandingsService standingsService;

    public List<FootballFixture> getAll() { return repository.findAll(); }

    public boolean hasLiveMatches() {
        return repository.existsByStatus(FixtureStatus.LIVE);
    }

    public FootballFixture create(FixtureRequest req) {
        FootballFixture fixture = FootballFixture.builder()
                .leagueId(req.getLeagueId()).homeClubId(req.getHomeClubId())
                .awayClubId(req.getAwayClubId()).status(req.getStatus())
                .kickoffAt(req.getKickoffAt()).matchday(req.getMatchday())
                .build();
        return repository.save(fixture);
    }

    public FootballFixture update(String id, FixtureRequest req) {
        FootballFixture fixture = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Fixture not found"));
        fixture.setLeagueId(req.getLeagueId()); fixture.setHomeClubId(req.getHomeClubId());
        fixture.setAwayClubId(req.getAwayClubId()); fixture.setStatus(req.getStatus());
        fixture.setKickoffAt(req.getKickoffAt()); fixture.setMatchday(req.getMatchday());
        return repository.save(fixture);
    }

    public FootballFixture updateScore(String id, ScoreUpdateRequest req) {
        FootballFixture fixture = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Fixture not found"));
        fixture.setHomeScore(req.getHomeScore());
        fixture.setAwayScore(req.getAwayScore());
        fixture.setStatus(FixtureStatus.FINISHED);
        FootballFixture saved = repository.save(fixture);

        // Recalculate standings when a match score is updated/finished
        standingsService.recalculateStandings(saved.getLeagueId());

        return saved;
    }
}