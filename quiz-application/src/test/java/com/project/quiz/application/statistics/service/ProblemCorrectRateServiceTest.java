package com.project.quiz.application.statistics.service;

import com.project.quiz.application.statistics.repository.ProblemCorrectRateCacheEntry;
import com.project.quiz.application.statistics.repository.ProblemCorrectRateCacheRepository;
import com.project.quiz.application.statistics.repository.ProblemCorrectRateQueryRepository;
import com.project.quiz.domain.statistics.ProblemCorrectRatePolicy;
import com.project.quiz.domain.statistics.ProblemCorrectRateSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProblemCorrectRateServiceTest {

    @Mock
    private ProblemCorrectRateQueryRepository problemCorrectRateQueryRepository;

    @Mock
    private ProblemCorrectRateCacheRepository problemCorrectRateCacheRepository;

    private ProblemCorrectRateService problemCorrectRateService;

    @BeforeEach
    void setUp() {
        problemCorrectRateService = new ProblemCorrectRateService(
                problemCorrectRateQueryRepository,
                problemCorrectRateCacheRepository,
                new ProblemCorrectRatePolicy()
        );
    }

    @Test
    void returnCachedCorrectRateWhenCacheHit() {
        when(problemCorrectRateCacheRepository.find(100L))
                .thenReturn(Optional.of(new ProblemCorrectRateCacheEntry(68)));

        Integer correctRate = problemCorrectRateService.getCorrectRate(100L);

        assertThat(correctRate).isEqualTo(68);
        verifyNoInteractions(problemCorrectRateQueryRepository);
    }

    @Test
    void cacheCalculatedCorrectRateWhenCacheMiss() {
        when(problemCorrectRateCacheRepository.find(100L)).thenReturn(Optional.empty());
        when(problemCorrectRateQueryRepository.findCorrectRateSummary(100L))
                .thenReturn(Optional.of(new ProblemCorrectRateSummary(30L, 20L)));

        Integer correctRate = problemCorrectRateService.getCorrectRate(100L);

        assertThat(correctRate).isEqualTo(67);
        verify(problemCorrectRateCacheRepository).put(100L, 67);
    }
}
