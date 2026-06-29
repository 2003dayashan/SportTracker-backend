package com.example.SportsTracker.questboard.service;

import com.example.SportsTracker.questboard.model.Quest;
import com.example.SportsTracker.questboard.model.QuestSubmission;
import com.example.SportsTracker.questboard.model.User;
import com.example.SportsTracker.questboard.repository.CategoryRepository;
import com.example.SportsTracker.questboard.repository.QuestRepository;
import com.example.SportsTracker.questboard.repository.QuestSubmissionRepository;
import com.example.SportsTracker.questboard.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class QuestService {

    private final QuestRepository questRepository;
    private final QuestSubmissionRepository questSubmissionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public QuestService(QuestRepository questRepository,
                        QuestSubmissionRepository questSubmissionRepository,
                        UserRepository userRepository,
                        CategoryRepository categoryRepository) {
        this.questRepository = questRepository;
        this.questSubmissionRepository = questSubmissionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Retrieve a paginated list of quests with optional filters.
     * Only non-deleted quests are returned (deletedAt == null).
     *
     * @param page     page number (0-indexed)
     * @param size     page size
     * @param sort     sort direction, e.g., "title,asc" or "createdAt,desc"
     * @param categoryId optional category ID filter
     * @param status   optional quest status filter
     * @param keyword  optional keyword to search in title/description (case-insensitive)
     * @return Page of Quest entities (non-deleted)
     */
    public Page<Quest> getQuests(int page, int size, String sort,
                                 String categoryId,
                                 Quest.Status status,
                                 String keyword) {
        Sort.Direction direction = Sort.Direction.ASC;
        String[] parts = sort.split(",");
        if (parts.length == 2) {
            if (parts[1].equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }
        }
        Sort sortObj = Sort.by(direction, parts[0]);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        // Build query using repository methods that exclude deletedAt (soft-delete)
        if (categoryId != null && !categoryId.isEmpty() && status != null && keyword != null && !keyword.isEmpty()) {
            return questRepository.findByDeletedAtIsNullAndCategoryIdAndStatusAndTitleContainingIgnoreCase(
                    categoryId, status, keyword, pageable);
        } else if (categoryId != null && !categoryId.isEmpty() && status != null) {
            return questRepository.findByDeletedAtIsNullAndCategoryIdAndStatus(categoryId, status, pageable);
        } else if (categoryId != null && !categoryId.isEmpty() && keyword != null && !keyword.isEmpty()) {
            return questRepository.findByDeletedAtIsNullAndCategoryIdAndTitleContainingIgnoreCase(
                    categoryId, keyword, pageable);
        } else if (status != null && keyword != null && !keyword.isEmpty()) {
            return questRepository.findByDeletedAtIsNullAndStatusAndTitleContainingIgnoreCase(
                    status, keyword, pageable);
        } else if (categoryId != null && !categoryId.isEmpty()) {
            return questRepository.findByDeletedAtIsNullAndCategoryId(categoryId, pageable);
        } else if (status != null) {
            return questRepository.findByDeletedAtIsNullAndStatus(status, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            return questRepository.findByDeletedAtIsNullAndTitleContainingIgnoreCase(keyword, pageable);
        } else {
            return questRepository.findByDeletedAtIsNull(pageable);
        }
    }

    public Quest getQuestById(String id) {
        return questRepository.findById(id).orElse(null);
    }

    @Transactional
    public Quest createQuest(Quest quest) {
        // Ensure timestamps are set
        Date now = new Date();
        quest.setCreatedAt(now);
        quest.setUpdatedAt(now);
        // Ensure soft-delete flag is null
        quest.setDeletedAt(null);
        return questRepository.save(quest);
    }

    @Transactional
    public Quest updateQuest(String questId, Quest updatedQuest) {
        Quest existing = questRepository.findById(questId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found with id: " + questId));
        // Update mutable fields
        existing.setTitle(updatedQuest.getTitle());
        existing.setDescription(updatedQuest.getDescription());
        existing.setStatus(updatedQuest.getStatus());
        existing.setDifficulty(updatedQuest.getDifficulty());
        existing.setRewardXp(updatedQuest.getRewardXp());
        existing.setDeadline(updatedQuest.getDeadline());
        existing.setCategoryId(updatedQuest.getCategoryId());
        existing.setImageUrl(updatedQuest.getImageUrl());
        existing.setUpdatedAt(new Date());
        // Do not change createdBy, claimedBy, createdAt, deletedAt via this method
        return questRepository.save(existing);
    }

    @Transactional
    public void softDeleteQuest(String questId) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found with id: " + questId));
        quest.setDeletedAt(new Date());
        questRepository.save(quest);
    }

    @Transactional
    public Quest claimQuest(String questId, String userId) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found with id: " + questId));
        if (!Quest.Status.OPEN.equals(quest.getStatus())) {
            throw new IllegalStateException("Quest is not open for claiming");
        }
        // Optionally verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        quest.setStatus(Quest.Status.CLAIMED);
        quest.setClaimedBy(userId);
        quest.setUpdatedAt(new Date());
        return questRepository.save(quest);
    }

    @Transactional
    public QuestSubmission submitProof(String questId, String userId,
                                       String proofText, String fileUrl) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new IllegalArgumentException("Quest not found with id: " + questId));
        if (!Quest.Status.CLAIMED.equals(quest.getStatus()) ||
                !quest.getClaimedBy().equals(userId)) {
            throw new IllegalStateException("Quest must be claimed by the user to submit proof");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        QuestSubmission submission = new QuestSubmission();
        submission.setQuestId(questId);
        submission.setUserId(userId);
        submission.setProofText(proofText);
        submission.setProofFileUrl(fileUrl);
        submission.setStatus(QuestSubmission.Status.PENDING);
        submission.setSubmittedAt(new Date());

        // Update quest status to SUBMITTED
        quest.setStatus(Quest.Status.SUBMITTED);
        quest.setUpdatedAt(new Date());
        questRepository.save(quest);

        return questSubmissionRepository.save(submission);
    }

    @Transactional
    public QuestSubmission approveSubmission(String submissionId, boolean approved) {
        QuestSubmission submission = questSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found with id: " + submissionId));
        if (!QuestSubmission.Status.PENDING.equals(submission.getStatus())) {
            throw new IllegalStateException("Submission is not in PENDING state");
        }
        Quest quest = questRepository.findById(submission.getQuestId())
                .orElseThrow(() -> new IllegalArgumentException("Associated quest not found"));
        User user = userRepository.findById(submission.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Associated user not found"));

        if (approved) {
            submission.setStatus(QuestSubmission.Status.APPROVED);
            quest.setStatus(Quest.Status.COMPLETED);
            // Award XP
            int xpToAdd = quest.getRewardXp();
            user.setXp(user.getXp() + xpToAdd);
            // Optionally recalc level (simple example: level = xp / 100 + 1)
            user.setLevel(user.getXp() / 100 + 1);
            userRepository.save(user);
        } else {
            submission.setStatus(QuestSubmission.Status.REJECTED);
            quest.setStatus(Quest.Status.CLAIMED); // revert to claimed so user can try again?
            // Actually spec: if rejected, quest stays CLAIMED? We'll set to CLAIMED.
        }
        quest.setUpdatedAt(new Date());
        submission.setSubmittedAt(new Date()); // update timestamp on decision
        questRepository.save(quest);
        userRepository.save(user);
        return questSubmissionRepository.save(submission);
    }
}