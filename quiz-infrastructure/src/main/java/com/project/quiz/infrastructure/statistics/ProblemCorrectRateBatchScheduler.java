package com.project.quiz.infrastructure.statistics;

import com.project.quiz.application.statistics.service.ProblemCorrectRateCacheRefreshService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "quiz.statistics.batch", name = "enabled", havingValue = "true")
public class ProblemCorrectRateBatchScheduler {

    private final ProblemCorrectRateCacheRefreshService problemCorrectRateCacheRefreshService;

    @Scheduled(fixedDelayString = "${quiz.statistics.batch.refresh-ms:600000}")
    public void refreshCorrectRateCache() {
        problemCorrectRateCacheRefreshService.refreshAll();
    }
}
