package com.example.SportsTracker.questboard.controller;

import com.example.SportsTracker.questboard.model.Quest;
import com.example.SportsTracker.questboard.model.QuestSubmission;
import com.example.SportsTracker.questboard.service.FileStorageService;
import com.example.SportsTracker.questboard.service.QuestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/quests")
public class QuestController {

    private final QuestService questService;
    private final FileStorageService fileStorageService;

    public QuestController(QuestService questService, FileStorageService fileStorageService) {
        this.questService = questService;
        this.fileStorageService = fileStorageService;
    }

    // DTOs as Java records
    public record QuestRequest(
            String title,
            String description,
            String difficulty, // EASY, MEDIUM, HARD, LEGENDARY
            int rewardXp,
            String deadline,   // ISO date string yyyy-MM-dd
            String categoryId
    ) {}

    public record QuestResponse(
            String id,
            String title,
            String description,
            String status,
            String difficulty,
            int rewardXp,
            String deadline,
            String categoryId,
            String createdBy,
            String claimedBy,
            String imageUrl,
            boolean notDeleted
    ) {}

    public record SubmissionResponse(
            String id,
            String questId,
            String userId,
            String proofText,
            String proofFileUrl,
            String status,
            String submittedAt
    ) {}

    // Helper to get current user ID from SecurityContext (set by JwtAuthFilter)
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof String userId) {
            return userId;
        }
        return null;
    }

    // GET /api/quests?page=0&size=10&sort=title,asc&categoryId=&status=&keyword=
    @GetMapping
    public ResponseEntity<Page<QuestResponse>> getQuests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) Quest.Status status,
            @RequestParam(required = false) String keyword) {

        Sort.Direction direction = Sort.Direction.ASC;
        String[] parts = sort.split(",");
        if (parts.length == 2) {
            if (parts[1].equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }
        }
        Sort sortObj = Sort.by(direction, parts[0]);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<Quest> questPage = questService.getQuests(page, size, sort, categoryId, status, keyword);
        Page<QuestResponse> responsePage = questPage.map(this::toQuestResponse);
        return ResponseEntity.ok(responsePage);
    }

    // GET /api/quests/{id}
    @GetMapping("/{id}")
    public ResponseEntity<QuestResponse> getQuestById(@PathVariable String id) {
        Quest quest = questService.getQuestById(id);
        if (quest == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toQuestResponse(quest));
    }

    // POST /api/quests (GUILD_MASTER+)
    @PostMapping
    @PreAuthorize("hasRole('GUILD_MASTER') or hasRole('ADMIN')")
    public ResponseEntity<QuestResponse> createQuest(@RequestBody QuestRequest req) {
        Quest quest = new Quest();
        quest.setTitle(req.title);
        quest.setDescription(req.description);
        quest.setDifficulty(Quest.Difficulty.valueOf(req.difficulty.toUpperCase()));
        quest.setRewardXp(req.rewardXp);
        // parse deadline
        LocalDate ld = LocalDate.parse(req.deadline);
        Instant instant = ld.atStartOfDay(ZoneOffset.UTC).toInstant();
        quest.setDeadline(Date.from(instant));
        quest.setCategoryId(req.categoryId);
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        quest.setCreatedBy(userId);
        quest.setStatus(Quest.Status.OPEN);
        Quest saved = questService.createQuest(quest);
        return ResponseEntity.status(HttpStatus.CREATED).body(toQuestResponse(saved));
    }

    // PUT /api/quests/{id} (GUILD_MASTER+)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GUILD_MASTER') or hasRole('ADMIN')")
    public ResponseEntity<QuestResponse> updateQuest(@PathVariable String id, @RequestBody QuestRequest req) {
        Quest existing = questService.getQuestById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        // Optionally check ownership? For simplicity allow any guild master/admin to update any quest
        existing.setTitle(req.title);
        existing.setDescription(req.description);
        existing.setDifficulty(Quest.Difficulty.valueOf(req.difficulty.toUpperCase()));
        existing.setRewardXp(req.rewardXp);
        LocalDate ld = LocalDate.parse(req.deadline);
        Instant instant = ld.atStartOfDay(ZoneOffset.UTC).toInstant();
        existing.setDeadline(Date.from(instant));
        existing.setCategoryId(req.categoryId);
        existing.setUpdatedAt(new Date());
        Quest updated = questService.updateQuest(id, existing);
        return ResponseEntity.ok(toQuestResponse(updated));
    }

    // DELETE /api/quests/{id} (ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> deleteQuest(@PathVariable String id) {
        questService.softDeleteQuest(id);
        Map<String, Boolean> result = new HashMap<>();
        result.put("deleted", true);
        return ResponseEntity.ok(result);
    }

    // POST /api/quests/{id}/claim (USER)
    @PostMapping("/{id}/claim")
    @PreAuthorize("hasAnyRole('USER', 'GUILD_MASTER', 'ADMIN')")
    public ResponseEntity<QuestResponse> claimQuest(@PathVariable String id) {
        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Quest claimed = questService.claimQuest(id, userId);
        return ResponseEntity.ok(toQuestResponse(claimed));
    }

    // POST /api/quests/{id}/submit (multipart) (USER)
    @PostMapping(value = "/{id}/submit", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAnyRole('USER', 'GUILD_MASTER', 'ADMIN')")
    public ResponseEntity<SubmissionResponse> submitProof(
            @PathVariable String id,
            @RequestPart("proofText") String proofText,
            @RequestPart(value = "proofFile", required = false) MultipartFile proofFile) throws IOException {

        String userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Validate proofText not blank
        if (proofText == null || proofText.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Save file using FileStorageService
        String fileName = null;
        if (proofFile != null && !proofFile.isEmpty()) {
            fileName = fileStorageService.storeFile(proofFile);
        }

        QuestSubmission submission = questService.submitProof(id, userId, proofText, fileName);
        return ResponseEntity.status(HttpStatus.CREATED).body(toSubmissionResponse(submission));
    }

    // POST /api/quests/submissions/{id}/approve (GUILD_MASTER+)
    @PostMapping("/submissions/{id}/approve")
    @PreAuthorize("hasRole('GUILD_MASTER') or hasRole('ADMIN')")
    public ResponseEntity<SubmissionResponse> approveSubmission(
            @PathVariable String id,
            @RequestParam boolean approved) {
        QuestSubmission submission = questService.approveSubmission(id, approved);
        return ResponseEntity.ok(toSubmissionResponse(submission));
    }

    // Conversion helpers
    private QuestResponse toQuestResponse(Quest q) {
        return new QuestResponse(
                q.getId(),
                q.getTitle(),
                q.getDescription(),
                q.getStatus().name(),
                q.getDifficulty().name(),
                q.getRewardXp(),
                q.getDeadline() != null ? q.getDeadline().toString() : null,
                q.getCategoryId(),
                q.getCreatedBy(),
                q.getClaimedBy(),
                q.getImageUrl(),
                q.getDeletedAt() == null
        );
    }

    private SubmissionResponse toSubmissionResponse(QuestSubmission s) {
        return new SubmissionResponse(
                s.getId(),
                s.getQuestId(),
                s.getUserId(),
                s.getProofText(),
                s.getProofFileUrl(),
                s.getStatus().name(),
                s.getSubmittedAt() != null ? s.getSubmittedAt().toString() : null
        );
    }
}