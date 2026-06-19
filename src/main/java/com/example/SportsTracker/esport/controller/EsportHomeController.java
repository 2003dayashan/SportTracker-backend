package com.example.SportsTracker.esport.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EsportHomeController {
    @GetMapping("/esports")
    public String home() {
        return "esport/home";
    }
}