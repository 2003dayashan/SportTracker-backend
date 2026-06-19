package com.example.SportsTracker.football.service;

import com.example.SportsTracker.football.model.FixtureStatus;
import com.example.SportsTracker.football.model.FootballClub;
import com.example.SportsTracker.football.model.FootballFixture;
import com.example.SportsTracker.football.model.FootballStandings;
import com.example.SportsTracker.football.repository.FootballClubRepository;
import com.example.SportsTracker.football.repository.FootballFixtureRepository;
import com.example.SportsTracker.football.repository.FootballStandingsRepository;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FootballStandingsService {

    private final FootballStandingsRepository standingsRepository;
    private final FootballFixtureRepository fixtureRepository;
    private final FootballClubRepository clubRepository;

    public void recalculateStandings(String leagueId) {
        List<FootballFixture> fixtures = fixtureRepository.findByLeagueIdAndStatus(leagueId, FixtureStatus.FINISHED);
        List<FootballClub> clubs = clubRepository.findByLeagueId(leagueId);

        Map<String, FootballStandings> standingsMap = new HashMap<>();

        for (FootballClub club : clubs) {
            standingsMap.put(club.getId(), FootballStandings.builder()
                    .leagueId(leagueId)
                    .clubId(club.getId())
                    .clubName(club.getName())
                    .played(0).won(0).drawn(0).lost(0)
                    .goalsFor(0).goalsAgainst(0).goalDifference(0).points(0)
                    .season("Current")
                    .build());
        }

        for (FootballFixture f : fixtures) {
            FootballStandings home = standingsMap.get(f.getHomeClubId());
            FootballStandings away = standingsMap.get(f.getAwayClubId());

            if (home == null || away == null) continue;

            home.setPlayed(home.getPlayed() + 1);
            away.setPlayed(away.getPlayed() + 1);

            home.setGoalsFor(home.getGoalsFor() + f.getHomeScore());
            home.setGoalsAgainst(home.getGoalsAgainst() + f.getAwayScore());
            away.setGoalsFor(away.getGoalsFor() + f.getAwayScore());
            away.setGoalsAgainst(away.getGoalsAgainst() + f.getHomeScore());

            if (f.getHomeScore() > f.getAwayScore()) {
                home.setWon(home.getWon() + 1);
                home.setPoints(home.getPoints() + 3);
                away.setLost(away.getLost() + 1);
            } else if (f.getHomeScore() < f.getAwayScore()) {
                away.setWon(away.getWon() + 1);
                away.setPoints(away.getPoints() + 3);
                home.setLost(home.getLost() + 1);
            } else {
                home.setDrawn(home.getDrawn() + 1);
                home.setPoints(home.getPoints() + 1);
                away.setDrawn(away.getDrawn() + 1);
                away.setPoints(away.getPoints() + 1);
            }

            home.setGoalDifference(home.getGoalsFor() - home.getGoalsAgainst());
            away.setGoalDifference(away.getGoalsFor() - away.getGoalsAgainst());
        }

        List<FootballStandings> sortedStandings = standingsMap.values().stream()
                .sorted(Comparator.comparing(FootballStandings::getPoints).reversed()
                        .thenComparing(Comparator.comparing(FootballStandings::getGoalDifference).reversed())
                        .thenComparing(Comparator.comparing(FootballStandings::getGoalsFor).reversed()))
                .collect(Collectors.toList());

        standingsRepository.deleteByLeagueId(leagueId);

        for (int i = 0; i < sortedStandings.size(); i++) {
            sortedStandings.get(i).setPosition(i + 1);
        }

        standingsRepository.saveAll(sortedStandings);
    }

    public List<FootballStandings> getStandings(String leagueId) {
        return standingsRepository.findByLeagueIdOrderByPositionAsc(leagueId);
    }

    public void exportStandingsToCsv(String leagueId, HttpServletResponse response) throws IOException {
        List<FootballStandings> standings = getStandings(leagueId);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"standings_" + leagueId + ".csv\"");

        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            String[] header = {"Position", "Club", "Played", "Won", "Drawn", "Lost", "GF", "GA", "GD", "Points"};
            writer.writeNext(header);

            for (FootballStandings s : standings) {
                String[] row = {
                        String.valueOf(s.getPosition()),
                        s.getClubName(),
                        String.valueOf(s.getPlayed()),
                        String.valueOf(s.getWon()),
                        String.valueOf(s.getDrawn()),
                        String.valueOf(s.getLost()),
                        String.valueOf(s.getGoalsFor()),
                        String.valueOf(s.getGoalsAgainst()),
                        String.valueOf(s.getGoalDifference()),
                        String.valueOf(s.getPoints())
                };
                writer.writeNext(row);
            }
        }
    }
}