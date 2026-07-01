package com.example.SportsTracker.football.controller;

import com.example.SportsTracker.football.dto.ClubRequest;
import com.example.SportsTracker.football.model.FootballClub;
import com.example.SportsTracker.football.service.FootballClubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/football/clubs")
@RequiredArgsConstructor
