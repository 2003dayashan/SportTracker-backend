package com.example.SportsTracker.football.repository;

import com.example.SportsTracker.football.model.FanProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FanProfileRepository extends MongoRepository<FanProfile, String> {
    Optional<FanProfile> findByUserId(String userId);
}