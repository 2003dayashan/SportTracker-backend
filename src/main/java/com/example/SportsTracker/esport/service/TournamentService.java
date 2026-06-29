package com.example.SportsTracker.esport.service;

import com.example.SportsTracker.esport.dto.TournamentRequest;
import com.example.SportsTracker.esport.model.Tournament;
import com.example.SportsTracker.esport.model.TournamentStatus;
import com.example.SportsTracker.esport.repository.TournamentRepository;
import com.example.SportsTracker.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository repository;

    public Tournament createTournament(TournamentRequest request, String organizerId) {
        Tournament tournament = Tournament.builder()
                .name(request.getName())
                .game(request.getGame())
                .format(request.getFormat())
                .status(request.getStatus())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .maxTeams(request.getMaxTeams())
                .organizerId(organizerId)
                .teamIds(new ArrayList<>())
                .build();
        return repository.save(tournament);
    }

    public Page<Tournament> getTournaments(TournamentStatus status, String game, Pageable pageable) {
        if (status != null && game != null) return repository.findByStatusAndGame(status, game, pageable);
        if (status != null) return repository.findByStatus(status, pageable);
        if (game != null) return repository.findByGame(game, pageable);
        return repository.findAll(pageable);
    }



    public Tournament updateTournament(String id, TournamentRequest request) {
        Tournament t = getTournament(id);
        t.setName(request.getName());
        t.setGame(request.getGame());
        t.setFormat(request.getFormat());
        t.setStatus(request.getStatus());
        t.setStartDate(request.getStartDate());
        t.setEndDate(request.getEndDate());
        t.setMaxTeams(request.getMaxTeams());
        return repository.save(t);
    }

    public void deleteTournament(String id) {
        repository.deleteById(id);
    }

    // Auto-complete tournaments past endDate (Runs at midnight server time)
    @Scheduled(cron = "0 0 0 * * ?")
    public void autoCompleteTournaments() {
        List<Tournament> pastDue = repository.findByStatusAndEndDateBefore(TournamentStatus.ONGOING, LocalDateTime.now());
        pastDue.forEach(t -> t.setStatus(TournamentStatus.COMPLETED));
        repository.saveAll(pastDue);
    }

    public Tournament getTournament(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found: " + id));
    }

    public List<Tournament> searchTournaments(String query) {
        return repository.findByNameContainingIgnoreCase(query);
    }
}