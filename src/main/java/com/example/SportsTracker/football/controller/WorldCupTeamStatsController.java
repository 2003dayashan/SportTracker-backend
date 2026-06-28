package com.example.SportsTracker.football.controller;

import com.example.SportsTracker.exception.UnauthorizedException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/football/worldcup")
@RequiredArgsConstructor
public class WorldCupTeamStatsController {

    @Value("${football.api.token}")   // ← fix: .key → .token
    private String apiToken;

    private final RestTemplate restTemplate;

    private static final String BASE_URL = "https://api.football-data.org/v4";
    private static final String WC_CODE = "WC";

    private HttpHeaders headers() {
        HttpHeaders h = new HttpHeaders();
        h.set("X-Auth-Token", apiToken);
        return h;
    }

    @GetMapping("/team-stats")
    public ResponseEntity<Map<String, Object>> getTeamStats(
            @RequestParam String countryCode,
            HttpSession session) {

        String userId = (String) session.getAttribute("USER_ID");
        if (userId == null) throw new UnauthorizedException("User not authenticated");

        HttpEntity<Void> entity = new HttpEntity<>(headers());

        // Use same WC endpoint as WorldCupSyncService
        ResponseEntity<Map> standingsRes = restTemplate.exchange(
                BASE_URL + "/competitions/" + WC_CODE + "/standings",
                HttpMethod.GET, entity, Map.class);

        ResponseEntity<Map> matchesRes = restTemplate.exchange(
                BASE_URL + "/competitions/" + WC_CODE + "/matches",
                HttpMethod.GET, entity, Map.class);

        Map<String, Object> result = new HashMap<>();

        if (standingsRes.getBody() != null) {
            List<Map<String, Object>> standings =
                    (List<Map<String, Object>>) standingsRes.getBody().get("standings");

            if (standings != null) {
                outer:
                for (Map<String, Object> group : standings) {
                    List<Map<String, Object>> table =
                            (List<Map<String, Object>>) group.get("table");
                    if (table == null) continue;

                    for (Map<String, Object> row : table) {
                        Map<String, Object> team = (Map<String, Object>) row.get("team");
                        if (team == null) continue;
                        String tla = (String) team.get("tla");

                        if (countryCode.equalsIgnoreCase(tla)) {
                            result.put("groupName", group.get("group"));
                            result.put("groupTable", table);
                            result.put("teamRow", row);
                            result.put("teamName", team.get("name"));
                            result.put("teamCrest", team.get("crest"));
                            break outer;
                        }
                    }
                }
            }
        }

        if (matchesRes.getBody() != null) {
            List<Map<String, Object>> matches =
                    (List<Map<String, Object>>) matchesRes.getBody().get("matches");

            List<Map<String, Object>> teamMatches = new ArrayList<>();
            Map<String, Object> nextMatch = null;

            if (matches != null) {
                for (Map<String, Object> m : matches) {
                    Map<String, Object> home = (Map<String, Object>) m.get("homeTeam");
                    Map<String, Object> away = (Map<String, Object>) m.get("awayTeam");
                    String homeTla = home != null ? (String) home.get("tla") : "";
                    String awayTla = away != null ? (String) away.get("tla") : "";

                    if (countryCode.equalsIgnoreCase(homeTla) ||
                            countryCode.equalsIgnoreCase(awayTla)) {
                        teamMatches.add(m);
                        String status = (String) m.get("status");
                        if (nextMatch == null &&
                                ("SCHEDULED".equals(status) || "TIMED".equals(status))) {
                            nextMatch = m;
                        }
                    }
                }
            }

            result.put("matches", teamMatches);
            result.put("nextMatch", nextMatch);
        }

        return ResponseEntity.ok(result);
    }
}