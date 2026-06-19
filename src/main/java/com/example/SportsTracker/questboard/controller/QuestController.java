package com.example.SportsTracker.questboard.controller;

import com.example.SportsTracker.core.model.Role;
import com.example.SportsTracker.questboard.dto.QuestRequest;
import com.example.SportsTracker.questboard.dto.ReviewSubmissionRequest;
import com.example.SportsTracker.questboard.model.Leaderboard;
import com.example.SportsTracker.questboard.model.Quest;
import com.example.SportsTracker.questboard.model.QuestCategory;
import com.example.SportsTracker.questboard.model.QuestSubmission;
import com.example.SportsTracker.questboard.service.QuestService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/quests")
@RequiredArgsConstructor
public class QuestController {

    private final QuestService questService;

    // --- Quests CRUD ---
    @GetMapping
    public ResponseEntity<Page<Quest>> getQuests(
            @RequestParam(required = false) QuestCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(questService.getQuests(category, PageRequest.of(page, size)));
    }

    @PostMapping
    public ResponseEntity<Quest> createQuest(@Valid @RequestBody QuestRequest request, HttpSession session) {
        String userId = (String) session.getAttribute("USER_ID");
        return ResponseEntity.status(HttpStatus.CREATED).body(questService.createQuest(request, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quest> updateQuest(@PathVariable String id, @Valid @RequestBody QuestRequest request) {
        return ResponseEntity.ok(questService.updateQuest(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuest(@PathVariable String id) {
        questService.deleteQuest(id);
        return ResponseEntity.noContent().build();
    }

    // --- Submissions & Reviews ---
    @PostMapping(value = "/{id}/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QuestSubmission> submitQuest(
            @PathVariable("id") String questId,
            @RequestParam("proof") MultipartFile proof,
            HttpSession session) {
        String userId = (String) session.getAttribute("USER_ID");
        return ResponseEntity.status(HttpStatus.CREATED).body(questService.submitQuest(questId, userId, proof));
    }

    @PutMapping("/submissions/{id}/review")
    public ResponseEntity<QuestSubmission> reviewSubmission(
            @PathVariable("id") String submissionId,
            @Valid @RequestBody ReviewSubmissionRequest request,
            HttpSession session) {
        String reviewerId = (String) session.getAttribute("USER_ID");
        @SuppressWarnings("unchecked")
        List<Role> roles = (List<Role>) session.getAttribute("USER_ROLES");

        return ResponseEntity.ok(questService.reviewSubmission(submissionId, request, reviewerId, roles));
    }

    // --- Leaderboard ---
    @GetMapping("/leaderboard")
    public ResponseEntity<Page<Leaderboard>> getLeaderboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(questService.getLeaderboard(PageRequest.of(page, size)));
    }
}