package com.example.SportsTracker.football.service;

import com.example.SportsTracker.football.model.WorldCupMatch;
import com.example.SportsTracker.football.model.WorldCupStanding;
import com.example.SportsTracker.football.repository.WorldCupMatchRepository;
import com.example.SportsTracker.football.repository.WorldCupStandingRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorldCupSyncService {

    private final RestTemplate restTemplate;
    private final WorldCupStandingRepository standingRepository;
    private final WorldCupMatchRepository matchRepository;

    @Value("${football.api.token}")
    private String apiToken;

    private static final String BASE_URL =
            "https://api.football-data.org/v4/competitions/WC";

    // Every 10 minutes
    @Scheduled(fixedRate = 600000)
    public void syncWorldCupData() {
        log.info("Starting World Cup data sync...");
        syncStandings();
        syncMatches();
    }

    public void syncStandings() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Auth-Token", apiToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    BASE_URL + "/standings",
                    HttpMethod.GET, entity, JsonNode.class);

            JsonNode standingsArray = response.getBody().path("standings");

            // Clear old data and re-save fresh
            standingRepository.deleteAll();

            for (JsonNode group : standingsArray) {
                String groupName = group.path("group").asText();
                JsonNode table = group.path("table");

                for (JsonNode row : table) {
                    WorldCupStanding standing = new WorldCupStanding();
                    standing.setGroupName(groupName);
                    standing.setPosition(row.path("position").asInt());
                    standing.setTeamName(
                            row.path("team").path("name").asText());
                    standing.setTeamCrestUrl(
                            row.path("team").path("crest").asText());
                    standing.setPlayedGames(row.path("playedGames").asInt());
                    standing.setWon(row.path("won").asInt());
                    standing.setDraw(row.path("draw").asInt());
                    standing.setLost(row.path("lost").asInt());
                    standing.setPoints(row.path("points").asInt());
                    standing.setGoalsFor(row.path("goalsFor").asInt());
                    standing.setGoalsAgainst(row.path("goalsAgainst").asInt());
                    standing.setGoalDifference(
                            row.path("goalDifference").asInt());
                    standing.setLastSyncedAt(LocalDateTime.now());
                    standingRepository.save(standing);
                }
            }
            log.info("World Cup standings synced successfully");
        } catch (Exception e) {
            log.error("Failed to sync standings: {}", e.getMessage());
        }
    }

    public void syncMatches() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Auth-Token", apiToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    BASE_URL + "/matches",
                    HttpMethod.GET, entity, JsonNode.class);

            JsonNode matchesArray = response.getBody().path("matches");

            for (JsonNode m : matchesArray) {
                Long externalId = m.path("id").asLong();

                WorldCupMatch match = matchRepository
                        .findByExternalMatchId(externalId)
                        .orElse(new WorldCupMatch());

                JsonNode fullTime = m.path("score").path("fullTime");

                match.setExternalMatchId(externalId);
                match.setGroupName(
                        m.path("group").isNull() ? null
                                : m.path("group").asText());
                match.setStage(m.path("stage").asText());
                match.setHomeTeam(
                        m.path("homeTeam").path("name").asText());
                match.setHomeTeamCrestUrl(
                        m.path("homeTeam").path("crest").asText());
                match.setAwayTeam(
                        m.path("awayTeam").path("name").asText());
                match.setAwayTeamCrestUrl(
                        m.path("awayTeam").path("crest").asText());
                match.setHomeScore(
                        fullTime.path("home").isNull() ? null
                                : fullTime.path("home").asInt());
                match.setAwayScore(
                        fullTime.path("away").isNull() ? null
                                : fullTime.path("away").asInt());
                match.setStatus(m.path("status").asText());

                String utcDate = m.path("utcDate").asText();
                match.setKickoffAt(
                        LocalDateTime.parse(utcDate.replace("Z", "")));
                match.setLastSyncedAt(LocalDateTime.now());

                matchRepository.save(match);
            }
            log.info("World Cup matches synced successfully");
        } catch (Exception e) {
            log.error("Failed to sync matches: {}", e.getMessage());
        }
    }
}