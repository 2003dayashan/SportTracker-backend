package com.example.SportsTracker.football.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "football_fan_profiles")
public class FanProfile {
    @Id
    private String id;
    private String userId;
    private String displayName;
    private String countryCode;
}