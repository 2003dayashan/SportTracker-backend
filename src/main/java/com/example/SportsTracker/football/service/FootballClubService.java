package com.example.SportsTracker.football.service;

import com.example.SportsTracker.exception.ResourceNotFoundException;
import com.example.SportsTracker.football.dto.ClubRequest;
import com.example.SportsTracker.football.model.FootballClub;
import com.example.SportsTracker.football.repository.FootballClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FootballClubService {
    private final FootballClubRepository repository;

    public List<FootballClub> getAll() { return repository.findAll(); }

    public FootballClub create(ClubRequest req) {
        FootballClub club = FootballClub.builder()
                .name(req.getName()).leagueId(req.getLeagueId())
                .country(req.getCountry()).stadiumName(req.getStadiumName())
                .badgeUrl(req.getBadgeUrl()).foundedYear(req.getFoundedYear()).build();
        return repository.save(club);
    }

    public FootballClub update(String id, ClubRequest req) {
        FootballClub club = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Club not found"));
        club.setName(req.getName()); club.setLeagueId(req.getLeagueId());
        club.setCountry(req.getCountry()); club.setStadiumName(req.getStadiumName());
        club.setBadgeUrl(req.getBadgeUrl()); club.setFoundedYear(req.getFoundedYear());
        return repository.save(club);
    }
}