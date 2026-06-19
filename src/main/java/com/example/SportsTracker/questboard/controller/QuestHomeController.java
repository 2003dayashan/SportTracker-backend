package com.example.SportsTracker.questboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QuestHomeController {
    @GetMapping("/quests")
    public String home() {
        return "questboard/home";
    }
}