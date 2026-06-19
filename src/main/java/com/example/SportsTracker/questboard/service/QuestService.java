package com.example.SportsTracker.questboard.service;

import com.example.SportsTracker.core.model.Role;
import com.example.SportsTracker.core.model.User;
import com.example.SportsTracker.core.repository.UserRepository;
import com.example.SportsTracker.exception.DuplicateResourcesException;
import com.example.SportsTracker.exception.ResourceNotFoundException;
import com.example.SportsTracker.exception.UnauthorizedException;
import com.example.SportsTracker.questboard.dto.QuestRequest;
import com.example.SportsTracker.questboard.dto.ReviewSubmissionRequest;
import com.example.SportsTracker.questboard.model.*;
import com.example.SportsTracker.questboard.repository.LeaderboardRepository;
import com.example.SportsTracker.questboard.repository.QuestRepository;
import com.example.SportsTracker.questboard.repository.QuestSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestRepository questRepository;
    private final QuestSubmissionRepository submissionRepository;
    private final LeaderboardRepository leaderboardRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    // --- Quests ---
    public Quest createQuest(QuestRequest request, String adminId) {
        Quest quest = Quest.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .pointsReward(request.getPointsReward())
                .status(request.getStatus())
                .expiresAt(request.getExpiresAt())
                .createdByUserId(adminId)
                .build();
        return questRepository.save(quest);
    }

    public Page<Quest> getQuests(QuestCategory category, Pageable pageable) {
        if (category != null) {
            return questRepository.findByCategory(category, pageable);
        }
        return questRepository.findAll(pageable);
    }

    public Quest updateQuest(String id, QuestRequest request) {
        Quest quest = questRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Quest not found"));
        quest.setTitle(request.getTitle());
        quest.setDescription(request.getDescription());
        quest.setCategory(request.getCategory());
        quest.setPointsReward(request.getPointsReward());
        quest.setStatus(request.getStatus());
        quest.setExpiresAt(request.getExpiresAt());
        return questRepository.save(quest);
    }

    public void deleteQuest(String id) {
        questRepository.deleteById(id);
    }

    // --- Submissions ---
    public QuestSubmission submitQuest(String questId, String userId, MultipartFile proof) {
        if (submissionRepository.findByQuestIdAndUserId(questId, userId).isPresent()) {
            throw new DuplicateResourcesException("You have already submitted proof for this quest.");
        }

        String proofUrl = fileStorageService.storeFile(proof);

        QuestSubmission submission = QuestSubmission.builder()
                .questId(questId)
                .userId(userId)
                .status(SubmissionStatus.PENDING)
                .proofUrl(proofUrl)
                .build();

        return submissionRepository.save(submission);
    }

    public QuestSubmission reviewSubmission(String submissionId, ReviewSubmissionRequest request, String reviewerId, List<Role> reviewerRoles) {
        if (reviewerRoles == null || !reviewerRoles.contains(Role.ROLE_ADMIN)) {
            throw new UnauthorizedException("Only admins can review submissions");
        }

        QuestSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        if (submission.getStatus() != SubmissionStatus.PENDING) {
            throw new IllegalArgumentException("Submission has already been reviewed");
        }

        submission.setStatus(request.getStatus());
        submission.setReviewedAt(LocalDateTime.now());
        submission.setReviewedByUserId(reviewerId);

        QuestSubmission updatedSubmission = submissionRepository.save(submission);

        if (request.getStatus() == SubmissionStatus.APPROVED) {
            Quest quest = questRepository.findById(submission.getQuestId())
                    .orElseThrow(() -> new ResourceNotFoundException("Quest not found"));
            updateLeaderboard(submission.getUserId(), quest.getPointsReward());
        }

        return updatedSubmission;
    }

    // --- Leaderboard ---
    private void updateLeaderboard(String userId, int points) {
        Leaderboard leaderboard = leaderboardRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            return Leaderboard.builder()
                    .userId(userId)
                    .username(user.getUsername())
                    .totalPoints(0)
                    .questsCompleted(0)
                    .build();
        });

        leaderboard.setTotalPoints(leaderboard.getTotalPoints() + points);
        leaderboard.setQuestsCompleted(leaderboard.getQuestsCompleted() + 1);
        leaderboardRepository.save(leaderboard);

        // Note: Rank recalculation can be handled via a scheduled task for performance at scale.
    }

    public Page<Leaderboard> getLeaderboard(Pageable pageable) {
        return leaderboardRepository.findAllByOrderByTotalPointsDesc(pageable);
    }
}