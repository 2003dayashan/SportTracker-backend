package com.example.SportsTracker.esport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StandingDto {
    private String teamId;
    private String teamName;
    private int wins;
    private int losses;
    private int points;
    private int played;
}