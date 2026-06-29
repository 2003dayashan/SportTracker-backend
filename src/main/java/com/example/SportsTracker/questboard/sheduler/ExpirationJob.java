package com.example.SportsTracker.questboard.sheduler;

import com.example.SportsTracker.questboard.model.Quest;
import com.example.SportsTracker.questboard.repository.QuestRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ExpirationJob {

    private final QuestRepository questRepository;

    public ExpirationJob(QuestRepository questRepository) {
        this.questRepository = questRepository;
    }

    // Runs every day at 00:00 (midnight)
    @Scheduled(cron = "0 0 0 * * *")
    public void expireOldClaimedQuests() {
        Date now = new Date();
        List<Quest> expiredCandidates = questRepository.findByStatusAndDeadlineBefore(Quest.Status.CLAIMED, now);
        for (Quest quest : expiredCandidates) {
            quest.setStatus(Quest.Status.EXPIRED);
            quest.setUpdatedAt(now);
        }
        if (!expiredCandidates.isEmpty()) {
            questRepository.saveAll(expiredCandidates);
        }
    }
}