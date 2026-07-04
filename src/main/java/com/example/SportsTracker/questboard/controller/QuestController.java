package com.example.SportsTracker.questboard.controller;

import com.example.SportsTracker.questboard.model.Quest;
import com.example.SportsTracker.questboard.model.QuestSubmission;
import com.example.SportsTracker.questboard.model.ServiceType;
import com.example.SportsTracker.questboard.service.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quests")
public class QuestController {

    @Autowired
    private QuestService questService;

    @GetMapping
    public Page<Quest> getAllQuests(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return questService.getAllQuests(page, size);
    }

    @GetMapping("/service/{serviceType}")
    public Page<Quest> getQuestsByService(@PathVariable ServiceType serviceType, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return questService.getQuestsByService(serviceType, page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quest> getQuestById(@PathVariable String id) {
        Quest quest = questService.getQuestById(id);
        return quest != null ? ResponseEntity.ok(quest) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Quest createQuest(@RequestBody Quest quest) {
        return questService.createQuest(quest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quest> updateQuest(@PathVariable String id, @RequestBody Quest quest) {
        Quest updated = questService.updateQuest(id, quest);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
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
    public Page<QuestSubmission> getPendingSubmissions(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return questService.getPendingSubmissions(page, size);
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
