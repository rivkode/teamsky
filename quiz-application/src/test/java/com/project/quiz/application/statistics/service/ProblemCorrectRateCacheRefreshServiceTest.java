package com.project.quiz.application.statistics.service;

import com.project.quiz.application.statistics.repository.ProblemCorrectRateCacheRepository;
import com.project.quiz.application.statistics.repository.ProblemStatisticsQueryRepository;
import com.project.quiz.domain.statistics.ProblemStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProblemCorrectRateCacheRefreshServiceTest {

    @Mock
    private ProblemStatisticsQueryRepository problemStatisticsQueryRepository;

    @Mock
    private ProblemCorrectRateCacheRepository problemCorrectRateCacheRepository;

    private ProblemCorrectRateCacheRefreshService problemCorrectRateCacheRefreshService;

    @BeforeEach
    void setUp() {
        problemCorrectRateCacheRefreshService = new ProblemCorrectRateCacheRefreshService(
                problemStatisticsQueryRepository,
                problemCorrectRateCacheRepository
        );
    }

    @Test
    void refreshAllWritesEveryStatisticsRowToCache() {
        when(problemStatisticsQueryRepository.findAllStatistics()).thenReturn(List.of(
                new ProblemStatistics(1001L, 31L, 21L, 68),
                new ProblemStatistics(1004L, 10L, 7L, null)
        ));

        problemCorrectRateCacheRefreshService.refreshAll();

        verify(problemCorrectRateCacheRepository).put(1001L, 68);
        verify(problemCorrectRateCacheRepository).put(1004L, null);
    }
}
