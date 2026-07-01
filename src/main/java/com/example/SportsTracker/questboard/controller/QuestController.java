package com.example.SportsTracker.questboard.controller;

import com.example.SportsTracker.questboard.model.Quest;
import com.example.SportsTracker.questboard.model.QuestSubmission;
import com.example.SportsTracker.questboard.model.ServiceType;
import com.example.SportsTracker.questboard.service.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quests")
public class QuestController {

    @Autowired
    private QuestService questService;

    @GetMapping
    public List<Quest> getAllQuests() {
        return questService.getAllQuests();
    }

    @GetMapping("/service/{serviceType}")
    public List<Quest> getQuestsByService(@PathVariable ServiceType serviceType) {
        return questService.getQuestsByService(serviceType);
    }

    @PostMapping
    public Quest createQuest(@RequestBody Quest quest) {
        return questService.createQuest(quest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuest(@PathVariable String id) {
        questService.deleteQuest(id);
        return ResponseEntity.noContent().build();
    }

    // --- Submissions ---

    @PostMapping("/{id}/claim")
    public ResponseEntity<QuestSubmission> claimQuest(@PathVariable String id, Authentication auth) {
        if (auth == null || auth.getName() == null) return ResponseEntity.status(401).build();
        QuestSubmission sub = questService.claimQuest(id, auth.getName());
        return sub != null ? ResponseEntity.ok(sub) : ResponseEntity.badRequest().build();
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<QuestSubmission> submitQuest(@PathVariable String id, Authentication auth) {
        if (auth == null || auth.getName() == null) return ResponseEntity.status(401).build();
        QuestSubmission sub = questService.submitQuest(id, auth.getName());
        return sub != null ? ResponseEntity.ok(sub) : ResponseEntity.badRequest().build();
    }

    @GetMapping("/submissions/pending")
    public List<QuestSubmission> getPendingSubmissions() {
        return questService.getPendingSubmissions();
    }

    @PutMapping("/submissions/{submissionId}/approve")
    public ResponseEntity<QuestSubmission> approveSubmission(@PathVariable String submissionId) {
        QuestSubmission sub = questService.approveSubmission(submissionId);
        return sub != null ? ResponseEntity.ok(sub) : ResponseEntity.notFound().build();
    }

    // --- Stats ---

    @GetMapping("/my-progress")
    public ResponseEntity<Map<String, Object>> getMyProgress(Authentication auth) {
        if (auth == null || auth.getName() == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(questService.getMyProgress(auth.getName()));
    }

    @GetMapping("/leaderboard")
    public List<Map<String, Object>> getLeaderboard() {
        return questService.getLeaderboard();
    }
}
