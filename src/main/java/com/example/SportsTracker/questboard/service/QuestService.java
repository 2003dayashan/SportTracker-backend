package com.example.SportsTracker.questboard.service;

import com.example.SportsTracker.core.model.User;
import com.example.SportsTracker.core.repository.UserRepository;
import com.example.SportsTracker.questboard.model.Quest;
import com.example.SportsTracker.questboard.model.QuestSubmission;
import com.example.SportsTracker.questboard.model.ServiceType;
import com.example.SportsTracker.questboard.model.SubmissionStatus;
import com.example.SportsTracker.questboard.repository.QuestRepository;
import com.example.SportsTracker.questboard.repository.QuestSubmissionRepository;
import com.example.SportsTracker.football.service.FootballFixtureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuestService {

    @Autowired
    private QuestRepository questRepository;
    
    @Autowired
    private QuestSubmissionRepository submissionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FootballFixtureService footballFixtureService;

    public Page<Quest> getAllQuests(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return questRepository.findAll(pageable);
    }

    public Page<Quest> getQuestsByService(ServiceType serviceType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return questRepository.findByServiceType(serviceType, pageable);
    }

    public Quest getQuestById(String id) {
        return questRepository.findById(id).orElse(null);
    }

    public Quest createQuest(Quest quest) {
        quest.setCompleted(false);
        return questRepository.save(quest);
    }

    public Quest updateQuest(String id, Quest updatedQuest) {
        return questRepository.findById(id).map(existingQuest -> {
            existingQuest.setTitle(updatedQuest.getTitle());
            existingQuest.setDescription(updatedQuest.getDescription());
            existingQuest.setPoints(updatedQuest.getPoints());
            existingQuest.setServiceType(updatedQuest.getServiceType());
            existingQuest.setDifficulty(updatedQuest.getDifficulty());
            existingQuest.setLiveEventRelated(updatedQuest.isLiveEventRelated());
            existingQuest.setRelatedMatchId(updatedQuest.getRelatedMatchId());
            return questRepository.save(existingQuest);
        }).orElse(null);
    }

    public void deleteQuest(String id) {
        questRepository.deleteById(id);
    }

    // --- Submissions ---

    public QuestSubmission claimQuest(String questId, String userId) {
        Optional<Quest> qOpt = questRepository.findById(questId);
        Optional<User> uOpt = userRepository.findById(userId);
        
        if (qOpt.isEmpty() || uOpt.isEmpty()) return null;
        
        Optional<QuestSubmission> existing = submissionRepository.findByUserIdAndQuestId(userId, questId);
        if (existing.isPresent()) {
            return existing.get();
        }

        Quest q = qOpt.get();
        User u = uOpt.get();
        
        QuestSubmission sub = new QuestSubmission();
        sub.setUserId(userId);
        sub.setUsername(u.getUsername());
        sub.setQuestId(questId);
        sub.setQuestTitle(q.getTitle());
        
        int awardedPoints = q.getPoints();
        if (q.isLiveEventRelated() && footballFixtureService.hasLiveMatches()) {
            awardedPoints = (int) (awardedPoints * 1.5);
        }
        sub.setPoints(awardedPoints);
        
        sub.setStatus(SubmissionStatus.CLAIMED);
        sub.setTimestamp(LocalDateTime.now());
        
        return submissionRepository.save(sub);
    }
    
    public QuestSubmission submitQuest(String questId, String userId) {
        Optional<QuestSubmission> existing = submissionRepository.findByUserIdAndQuestId(userId, questId);
        if (existing.isPresent()) {
            QuestSubmission sub = existing.get();
            if (sub.getStatus() == SubmissionStatus.CLAIMED) {
                sub.setStatus(SubmissionStatus.SUBMITTED);
                sub.setTimestamp(LocalDateTime.now());
                return submissionRepository.save(sub);
            }
            return sub;
        }
        return null;
    }

    public QuestSubmission approveSubmission(String submissionId) {
        Optional<QuestSubmission> existing = submissionRepository.findById(submissionId);
        if (existing.isPresent()) {
            QuestSubmission sub = existing.get();
            sub.setStatus(SubmissionStatus.APPROVED);
            sub.setTimestamp(LocalDateTime.now());
            return submissionRepository.save(sub);
        }
        return null;
    }
    
    public Page<QuestSubmission> getPendingSubmissions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return submissionRepository.findByStatus(SubmissionStatus.SUBMITTED, pageable);
    }
    
    // --- Leaderboard & Progress ---
    
    public Map<String, Object> getMyProgress(String userId) {
        List<QuestSubmission> subs = submissionRepository.findByUserId(userId);
        int totalPoints = subs.stream()
            .filter(s -> s.getStatus() == SubmissionStatus.APPROVED)
            .mapToInt(QuestSubmission::getPoints)
            .sum();
            
        Map<String, Object> progress = new HashMap<>();
        progress.put("points", totalPoints);
        progress.put("submissions", subs);
        return progress;
    }
    
    public List<Map<String, Object>> getLeaderboard() {
        List<QuestSubmission> approved = submissionRepository.findByStatus(SubmissionStatus.APPROVED);
        
        Map<String, Integer> userPoints = new HashMap<>();
        Map<String, String> userNames = new HashMap<>();
        
        for (QuestSubmission s : approved) {
            userPoints.put(s.getUserId(), userPoints.getOrDefault(s.getUserId(), 0) + s.getPoints());
            userNames.put(s.getUserId(), s.getUsername());
        }
        
        List<Map<String, Object>> leaderboard = new ArrayList<>();
        for (String uid : userPoints.keySet()) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("userId", uid);
            entry.put("username", userNames.get(uid));
            entry.put("points", userPoints.get(uid));
            leaderboard.add(entry);
        }
        
        leaderboard.sort((a, b) -> Integer.compare((Integer)b.get("points"), (Integer)a.get("points")));
        return leaderboard;
    }
}
