package com.example.SportsTracker.football.controller;

import com.example.SportsTracker.exception.UnauthorizedException;
import com.example.SportsTracker.football.dto.FanProfileRequest;
import com.example.SportsTracker.football.model.FanProfile;
import com.example.SportsTracker.football.service.FanProfileService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/football/fan-profile")
@RequiredArgsConstructor
public class FanProfileController {

    private final FanProfileService service;

    @GetMapping("/me")
    public ResponseEntity<FanProfile> getMyProfile(HttpSession session) {
        String userId = (String) session.getAttribute("USER_ID");
        if (userId == null) throw new UnauthorizedException("User not authenticated");
        return ResponseEntity.ok(service.getMyProfile(userId)); // null if not set yet
    }

    @PutMapping("/me")
    public ResponseEntity<FanProfile> saveMyProfile(@Valid @RequestBody FanProfileRequest request, HttpSession session) {
        String userId = (String) session.getAttribute("USER_ID");
        if (userId == null) throw new UnauthorizedException("User not authenticated");
        return ResponseEntity.ok(service.saveMyProfile(userId, request));
    }
}