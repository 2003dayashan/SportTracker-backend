package com.example.SportsTracker.esport.controller;

import com.example.SportsTracker.esport.dto.MatchScoreUpdateRequest;
import com.example.SportsTracker.esport.model.Match;
import com.example.SportsTracker.esport.model.MatchStatus;
import com.example.SportsTracker.esport.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

