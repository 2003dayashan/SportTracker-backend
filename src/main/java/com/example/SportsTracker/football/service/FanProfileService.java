package com.example.SportsTracker.football.service;

import com.example.SportsTracker.football.dto.FanProfileRequest;
import com.example.SportsTracker.football.model.FanProfile;
import com.example.SportsTracker.football.repository.FanProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FanProfileService {

    private final FanProfileRepository repository;

    public FanProfile getMyProfile(String userId) {
        return repository.findByUserId(userId).orElse(null);
    }

    public FanProfile saveMyProfile(String userId, FanProfileRequest request) {
        FanProfile profile = repository.findByUserId(userId).orElse(new FanProfile());
        profile.setUserId(userId);
        profile.setDisplayName(request.getDisplayName());
        profile.setCountryCode(request.getCountryCode());
        return repository.save(profile);
    }
}