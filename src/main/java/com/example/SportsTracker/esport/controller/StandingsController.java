package com.example.SportsTracker.esport.controller;

import com.example.SportsTracker.esport.dto.StandingDto;
import com.example.SportsTracker.esport.model.Match;
import com.example.SportsTracker.esport.model.MatchStatus;
import com.example.SportsTracker.esport.model.Team;
import com.example.SportsTracker.esport.repository.MatchRepository;
import com.example.SportsTracker.esport.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

