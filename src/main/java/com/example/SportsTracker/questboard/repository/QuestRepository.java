package com.example.SportsTracker.questboard.repository;

import com.example.SportsTracker.questboard.model.Quest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface QuestRepository extends MongoRepository<Quest, String> {
    Optional<Quest> findById(String id);
    List<Quest> findByStatus(Quest.Status status);
    Page<Quest> findByStatus(Quest.Status status, Pageable pageable);
    List<Quest> findByCreatedBy(String createdBy);
    Page<Quest> findByCreatedBy(String createdBy, Pageable pageable);
    List<Quest> findByClaimedBy(String claimedBy);
    Page<Quest> findByClaimedBy(String claimedBy, Pageable pageable);
    List<Quest> findByCategoryId(String categoryId);
    Page<Quest> findByCategoryId(String categoryId, Pageable pageable);
    List<Quest> findByTitleContainingIgnoreCase(String titleKeyword);
    Page<Quest> findByTitleContainingIgnoreCase(String titleKeyword, Pageable pageable);

    // Combined filters with pagination
    List<Quest> findByCategoryIdAndStatus(String categoryId, Quest.Status status);
    Page<Quest> findByCategoryIdAndStatus(String categoryId, Quest.Status status, Pageable pageable);

    List<Quest> findByCategoryIdAndTitleContainingIgnoreCase(String categoryId, String titleKeyword);
    Page<Quest> findByCategoryIdAndTitleContainingIgnoreCase(String categoryId, String titleKeyword, Pageable pageable);

    List<Quest> findByStatusAndTitleContainingIgnoreCase(Quest.Status status, String titleKeyword);
    Page<Quest> findByStatusAndTitleContainingIgnoreCase(Quest.Status status, String titleKeyword, Pageable pageable);

    List<Quest> findByCategoryIdAndStatusAndTitleContainingIgnoreCase(String categoryId, Quest.Status status, String titleKeyword);
    Page<Quest> findByCategoryIdAndStatusAndTitleContainingIgnoreCase(String categoryId, Quest.Status status, String titleKeyword, Pageable pageable);

    // Soft-delete aware queries (only non-deleted)
    List<Quest> findByDeletedAtIsNull();
    Page<Quest> findByDeletedAtIsNull(Pageable pageable);

    List<Quest> findByDeletedAtIsNullAndStatus(Quest.Status status);
    Page<Quest> findByDeletedAtIsNullAndStatus(Quest.Status status, Pageable pageable);

    List<Quest> findByDeletedAtIsNullAndCreatedBy(String createdBy);
    Page<Quest> findByDeletedAtIsNullAndCreatedBy(String createdBy, Pageable pageable);

    List<Quest> findByDeletedAtIsNullAndClaimedBy(String claimedBy);
    Page<Quest> findByDeletedAtIsNullAndClaimedBy(String claimedBy, Pageable pageable);

    List<Quest> findByDeletedAtIsNullAndCategoryId(String categoryId);
    Page<Quest> findByDeletedAtIsNullAndCategoryId(String categoryId, Pageable pageable);

    List<Quest> findByDeletedAtIsNullAndTitleContainingIgnoreCase(String titleKeyword);
    Page<Quest> findByDeletedAtIsNullAndTitleContainingIgnoreCase(String titleKeyword, Pageable pageable);

    // Combined with soft-delete
    List<Quest> findByDeletedAtIsNullAndCategoryIdAndStatus(String categoryId, Quest.Status status);
    Page<Quest> findByDeletedAtIsNullAndCategoryIdAndStatus(String categoryId, Quest.Status status, Pageable pageable);

    List<Quest> findByDeletedAtIsNullAndCategoryIdAndTitleContainingIgnoreCase(String categoryId, String titleKeyword);
    Page<Quest> findByDeletedAtIsNullAndCategoryIdAndTitleContainingIgnoreCase(String categoryId, String titleKeyword, Pageable pageable);

    List<Quest> findByDeletedAtIsNullAndStatusAndTitleContainingIgnoreCase(Quest.Status status, String titleKeyword);
    Page<Quest> findByDeletedAtIsNullAndStatusAndTitleContainingIgnoreCase(Quest.Status status, String titleKeyword, Pageable pageable);

    List<Quest> findByDeletedAtIsNullAndCategoryIdAndStatusAndTitleContainingIgnoreCase(String categoryId, Quest.Status status, String titleKeyword);
    Page<Quest> findByDeletedAtIsNullAndCategoryIdAndStatusAndTitleContainingIgnoreCase(String categoryId, Quest.Status status, String titleKeyword, Pageable pageable);

    // Expiration check for scheduler
    List<Quest> findByDeadlineBefore(Date date);
    List<Quest> findByStatusAndDeadlineBefore(Quest.Status status, Date date);
}