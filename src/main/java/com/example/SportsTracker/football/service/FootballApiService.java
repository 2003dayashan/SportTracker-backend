package com.example.SportsTracker.football.service;

import com.example.SportsTracker.football.model.*;
import com.example.SportsTracker.football.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FootballApiService {

    private final RestTemplate restTemplate;
    private final FootballLeagueRepository leagueRepository;
    private final FootballClubRepository clubRepository;
    private final FootballFixtureRepository fixtureRepository;
    private final FootballStandingsRepository standingsRepository;

    @Value("${football.api.token:YOUR_API_KEY_HERE}")
    private String apiKey;

    private static final String BASE_URL = "https://api.football-data.org/v4/competitions/";

    // ── Common headers (existing pattern) ──
    private HttpEntity<String> authEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", apiKey);
        return new HttpEntity<>("parameters", headers);
    }

    // ─────────────────────────────────────────────────────────────
    // 1. SYNC LEAGUE  (existing — untouched)
    // ─────────────────────────────────────────────────────────────
    public FootballLeague syncLeague(String code) {
        ResponseEntity<Map> response = restTemplate.exchange(
                BASE_URL + code, HttpMethod.GET, authEntity(), Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null) throw new RuntimeException("Failed to fetch league data");

        String name    = (String) body.get("name");
        String emblem  = (String) body.get("emblem");
        Map<String, Object> area = (Map<String, Object>) body.get("area");
        String country = area != null ? (String) area.get("name") : "Unknown";

        FootballLeague league = leagueRepository.findByApiCode(code)
                .orElse(new FootballLeague());

        league.setName(name);
        league.setApiCode(code);
        league.setCountry(country);
        league.setLogoUrl(emblem);
        league.setActive(true);
        if (league.getId() == null) {
            league.setCreatedAt(LocalDateTime.now());
        }

        return leagueRepository.save(league);
    }

    // ─────────────────────────────────────────────────────────────
    // 2. SYNC CLUBS   GET /v4/competitions/{code}/teams
    // ─────────────────────────────────────────────────────────────
    public void syncClubs(String leagueId, String code) {
        ResponseEntity<Map> response = restTemplate.exchange(
                BASE_URL + code + "/teams", HttpMethod.GET, authEntity(), Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null) throw new RuntimeException("Failed to fetch clubs");

        List<Map<String, Object>> teams = (List<Map<String, Object>>) body.get("teams");
        if (teams == null) return;

        for (Map<String, Object> team : teams) {
            String name = (String) team.get("name");

            // Update existing club or create new one
            FootballClub club = clubRepository
                    .findByNameAndLeagueId(name, leagueId)
                    .orElse(new FootballClub());

            club.setName(name);
            club.setLeagueId(leagueId);
            club.setBadgeUrl((String) team.get("crest"));

            // Venue
            Map<String, Object> venue = (Map<String, Object>) team.get("venue");
            if (venue != null) {
                club.setStadiumName((String) venue.get("name"));
            }

            // Founded year
            Object founded = team.get("founded");
            if (founded instanceof Integer) {
                club.setFoundedYear((Integer) founded);
            }

            clubRepository.save(club);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 3. SYNC FIXTURES  GET /v4/competitions/{code}/matches
    // ─────────────────────────────────────────────────────────────
    public void syncFixtures(String leagueId, String code) {
        ResponseEntity<Map> response = restTemplate.exchange(
                BASE_URL + code + "/matches", HttpMethod.GET, authEntity(), Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null) throw new RuntimeException("Failed to fetch fixtures");

        List<Map<String, Object>> matches = (List<Map<String, Object>>) body.get("matches");
        if (matches == null) return;

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        for (Map<String, Object> match : matches) {

            // Home / Away team names
            Map<String, Object> homeTeam = (Map<String, Object>) match.get("homeTeam");
            Map<String, Object> awayTeam = (Map<String, Object>) match.get("awayTeam");
            String homeName = homeTeam != null ? (String) homeTeam.get("name") : null;
            String awayName = awayTeam != null ? (String) awayTeam.get("name") : null;
            if (homeName == null || awayName == null) continue;

            // Look up club IDs from DB
            Optional<FootballClub> homeClubOpt = clubRepository.findByNameAndLeagueId(homeName, leagueId);
            Optional<FootballClub> awayClubOpt = clubRepository.findByNameAndLeagueId(awayName, leagueId);
            if (homeClubOpt.isEmpty() || awayClubOpt.isEmpty()) continue;

            // Score
            Map<String, Object> score      = (Map<String, Object>) match.get("score");
            Map<String, Object> fullTime   = score != null ? (Map<String, Object>) score.get("fullTime") : null;
            Integer homeScore = fullTime != null ? (Integer) fullTime.get("home") : null;
            Integer awayScore = fullTime != null ? (Integer) fullTime.get("away") : null;

            // Status
            String statusStr = (String) match.get("status");
            FixtureStatus status = switch (statusStr != null ? statusStr : "") {
                case "IN_PLAY", "PAUSED", "HALFTIME" -> FixtureStatus.LIVE;
                case "FINISHED"                       -> FixtureStatus.FINISHED;
                case "POSTPONED", "CANCELLED"         -> FixtureStatus.POSTPONED;
                default                               -> FixtureStatus.SCHEDULED;
            };

            // Kickoff time
            String utcDate = (String) match.get("utcDate");
            LocalDateTime kickoffAt = null;
            if (utcDate != null) {
                kickoffAt = LocalDateTime.parse(utcDate, formatter);
            }

            // Matchday
            Object matchdayObj = match.get("matchday");
            int matchday = matchdayObj instanceof Integer ? (Integer) matchdayObj : 1;

            // Save (always create fresh — avoid duplicates by clearing first if needed)
            FootballFixture fixture = new FootballFixture();
            fixture.setLeagueId(leagueId);
            fixture.setHomeClubId(homeClubOpt.get().getId());
            fixture.setAwayClubId(awayClubOpt.get().getId());
            fixture.setHomeScore(homeScore);
            fixture.setAwayScore(awayScore);
            fixture.setStatus(status);
            fixture.setKickoffAt(kickoffAt);
            fixture.setMatchday(matchday);

            fixtureRepository.save(fixture);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 4. SYNC STANDINGS  GET /v4/competitions/{code}/standings
    // ─────────────────────────────────────────────────────────────
    public void syncStandings(String leagueId, String code) {
        ResponseEntity<Map> response = restTemplate.exchange(
                BASE_URL + code + "/standings", HttpMethod.GET, authEntity(), Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null) throw new RuntimeException("Failed to fetch standings");

        List<Map<String, Object>> standingsGroups =
                (List<Map<String, Object>>) body.get("standings");
        if (standingsGroups == null || standingsGroups.isEmpty()) return;

        // football-data.org returns TOTAL / HOME / AWAY — we want TOTAL
        Map<String, Object> totalGroup = standingsGroups.stream()
                .filter(g -> "TOTAL".equals(g.get("type")))
                .findFirst()
                .orElse(standingsGroups.get(0));

        List<Map<String, Object>> table =
                (List<Map<String, Object>>) totalGroup.get("table");
        if (table == null) return;

        // Clear old standings for this league before re-syncing
        standingsRepository.deleteByLeagueId(leagueId);

        // Season string from response
        Map<String, Object> season = (Map<String, Object>) body.get("season");
        String seasonStr = season != null ? String.valueOf(season.get("startDate")).substring(0, 4) : "2024";

        for (Map<String, Object> row : table) {
            Map<String, Object> team = (Map<String, Object>) row.get("team");
            if (team == null) continue;

            FootballStandings s = new FootballStandings();
            s.setLeagueId(leagueId);
            s.setClubName((String) team.get("name"));
            s.setPosition((Integer) row.get("position"));
            s.setPlayed((Integer) row.get("playedGames"));
            s.setWon((Integer) row.get("won"));
            s.setDrawn((Integer) row.get("draw"));
            s.setLost((Integer) row.get("lost"));
            s.setGoalsFor((Integer) row.get("goalsFor"));
            s.setGoalsAgainst((Integer) row.get("goalsAgainst"));
            s.setGoalDifference((Integer) row.get("goalDifference"));
            s.setPoints((Integer) row.get("points"));
            s.setSeason(seasonStr);

            // Link club ID if exists in DB
            String clubName = (String) team.get("name");
            clubRepository.findByNameAndLeagueId(clubName, leagueId)
                    .ifPresent(club -> s.setClubId(club.getId()));

            standingsRepository.save(s);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 5. SYNC ALL — league + clubs + fixtures + standings එකවර
    // ─────────────────────────────────────────────────────────────
    public String syncAll(String code) {
        FootballLeague league = syncLeague(code);
        syncClubs(league.getId(), code);
        syncFixtures(league.getId(), code);
        syncStandings(league.getId(), code);
        return "Synced: " + league.getName();
    }
}