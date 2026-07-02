package com.example.SportsTracker.esport.controller;

import com.example.SportsTracker.esport.dto.PlayerRequest;
import com.example.SportsTracker.esport.model.Player;
import com.example.SportsTracker.esport.service.PlayerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

