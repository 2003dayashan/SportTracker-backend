package com.example.SportsTracker.esport.controller;

import com.example.SportsTracker.esport.model.Match;
import com.example.SportsTracker.esport.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

