package com.example.SportsTracker.football.service;

import com.example.SportsTracker.football.model.FootballLeague;
import com.example.SportsTracker.football.repository.FootballLeagueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FootballApiService {

    private final RestTemplate restTemplate;
    private final FootballLeagueRepository leagueRepository;

    @Value("${football.api.token:YOUR_API_KEY_HERE}")
    private String apiKey;

    private final String BASE_URL = "https://api.football-data.org/v4/competitions/";

    public FootballLeague syncLeague(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", apiKey);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                BASE_URL + code, HttpMethod.GET, entity, Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null) throw new RuntimeException("Failed to fetch league data");

        String name = (String) body.get("name");
        String emblem = (String) body.get("emblem");
        Map<String, Object> area = (Map<String, Object>) body.get("area");
        String country = area != null ? (String) area.get("name") : "Unknown";

        Optional<FootballLeague> existingOpt = leagueRepository.findByApiCode(code);
        FootballLeague league = existingOpt.orElse(new FootballLeague());

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
}