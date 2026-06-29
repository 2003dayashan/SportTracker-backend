package com.example.SportsTracker.esport.service;

import com.example.SportsTracker.esport.dto.MatchScoreUpdateRequest;
import com.example.SportsTracker.esport.model.Match;
import com.example.SportsTracker.esport.repository.MatchRepository;
import com.example.SportsTracker.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    public Match updateScore(String matchId, MatchScoreUpdateRequest request) {
        Match match = repository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        match.setScoreA(request.getScoreA());
        match.setScoreB(request.getScoreB());
        Match updatedMatch = repository.save(match);

        // Broadcast score update via WebSocket
        messagingTemplate.convertAndSend("/topic/matches/" + matchId, updatedMatch);

        return updatedMatch;
    }

    public Match createMatch(Match match) {
        return repository.save(match);
    }

    public Match getMatch(String id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Match not found"));
    }
}