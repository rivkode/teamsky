package com.project.quiz.application.statistics.service;

import com.project.quiz.application.statistics.repository.ProblemCorrectRateCacheRepository;
import com.project.quiz.application.statistics.repository.ProblemStatisticsCommandRepository;
import com.project.quiz.application.statistics.repository.ProblemUserStatisticsRepository;
import com.project.quiz.domain.solving.AnswerStatus;
import com.project.quiz.domain.statistics.ProblemStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProblemStatisticsUpdateServiceTest {

    @Mock
    private ProblemStatisticsCommandRepository problemStatisticsCommandRepository;

    @Mock
    private ProblemUserStatisticsRepository problemUserStatisticsRepository;

    @Mock
    private ProblemCorrectRateCacheRepository problemCorrectRateCacheRepository;

    private ProblemStatisticsUpdateService problemStatisticsUpdateService;

    @BeforeEach
    void setUp() {
        problemStatisticsUpdateService = new ProblemStatisticsUpdateService(
                problemStatisticsCommandRepository,
                problemUserStatisticsRepository,
                problemCorrectRateCacheRepository
        );
    }

    @Test
    void saveStatisticsAndWriteThroughCacheWhenFirstSolvedUser() {
        when(problemUserStatisticsRepository.trySaveFirstSolved(100L, 1L, AnswerStatus.CORRECT)).thenReturn(true);
        when(problemStatisticsCommandRepository.findStatisticsForUpdate(100L))
                .thenReturn(Optional.of(new ProblemStatistics(100L, 0L, 0L, null)));

        problemStatisticsUpdateService.recordSolvedProblem(100L, 1L, AnswerStatus.CORRECT);

        ArgumentCaptor<ProblemStatistics> statisticsCaptor = ArgumentCaptor.forClass(ProblemStatistics.class);
        verify(problemUserStatisticsRepository).trySaveFirstSolved(100L, 1L, AnswerStatus.CORRECT);
        verify(problemStatisticsCommandRepository).save(statisticsCaptor.capture());
        verify(problemCorrectRateCacheRepository).put(100L, null);

        ProblemStatistics savedStatistics = statisticsCaptor.getValue();
        assertThat(savedStatistics.problemId()).isEqualTo(100L);
        assertThat(savedStatistics.solvedUserCount()).isEqualTo(1L);
        assertThat(savedStatistics.correctUserCount()).isEqualTo(1L);
        assertThat(savedStatistics.correctRate()).isNull();
    }

    @Test
    void updateLatestAnswerStatusOnlyWhenUserAlreadyCounted() {
        when(problemUserStatisticsRepository.trySaveFirstSolved(100L, 1L, AnswerStatus.PARTIAL)).thenReturn(false);

        problemStatisticsUpdateService.recordSolvedProblem(100L, 1L, AnswerStatus.PARTIAL);

        verify(problemUserStatisticsRepository).updateLatestAnswerStatus(100L, 1L, AnswerStatus.PARTIAL);
        verifyNoMoreInteractions(problemStatisticsCommandRepository, problemCorrectRateCacheRepository);
    }

    @Test
    void loadAndUpdateStatisticsWhenStatisticsRowAlreadyExists() {
        when(problemUserStatisticsRepository.trySaveFirstSolved(100L, 1L, AnswerStatus.CORRECT)).thenReturn(true);
        when(problemStatisticsCommandRepository.findStatisticsForUpdate(100L))
                .thenReturn(Optional.of(new ProblemStatistics(100L, 5L, 4L, 80)));

        problemStatisticsUpdateService.recordSolvedProblem(100L, 1L, AnswerStatus.CORRECT);

        ArgumentCaptor<ProblemStatistics> statisticsCaptor = ArgumentCaptor.forClass(ProblemStatistics.class);
        verify(problemStatisticsCommandRepository).save(statisticsCaptor.capture());
        verify(problemCorrectRateCacheRepository).put(100L, null);

        ProblemStatistics savedStatistics = statisticsCaptor.getValue();
        assertThat(savedStatistics.solvedUserCount()).isEqualTo(6L);
        assertThat(savedStatistics.correctUserCount()).isEqualTo(5L);
        assertThat(savedStatistics.correctRate()).isNull();
    }
}
