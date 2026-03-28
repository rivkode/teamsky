package com.project.quiz.application.solving.service;

import com.project.quiz.application.solving.exception.ChapterNotFoundException;
import com.project.quiz.application.solving.exception.ProblemNotFoundInChapterException;
import com.project.quiz.application.solving.model.GetRandomProblemCommand;
import com.project.quiz.application.solving.model.GetRandomProblemResult;
import com.project.quiz.application.solving.model.SkipProblemCommand;
import com.project.quiz.application.solving.repository.ChapterRepository;
import com.project.quiz.application.solving.repository.ProblemRepository;
import com.project.quiz.application.solving.repository.SolveAttemptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkipProblemServiceTest {

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private SolveAttemptRepository solveAttemptRepository;

    @Mock
    private GetRandomProblemService getRandomProblemService;

    private SkipProblemService skipProblemService;

    @BeforeEach
    void setUp() {
        skipProblemService = new SkipProblemService(
                chapterRepository,
                problemRepository,
                solveAttemptRepository,
                getRandomProblemService
        );
    }

    @Test
    void chapterDoesNotExistThenThrowException() {
        SkipProblemCommand command = new SkipProblemCommand(1L, 99L, 100L);

        when(chapterRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> skipProblemService.skipProblem(command))
                .isInstanceOf(ChapterNotFoundException.class);
    }

    @Test
    void problemDoesNotBelongToChapterThenThrowException() {
        SkipProblemCommand command = new SkipProblemCommand(1L, 1L, 100L);

        when(chapterRepository.existsById(1L)).thenReturn(true);
        when(problemRepository.existsByIdAndChapterId(100L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> skipProblemService.skipProblem(command))
                .isInstanceOf(ProblemNotFoundInChapterException.class);
    }

    @Test
    void saveSkipAndReturnNextRandomProblem() {
        SkipProblemCommand command = new SkipProblemCommand(1L, 1L, 100L);
        GetRandomProblemResult nextProblem = new GetRandomProblemResult(101L, "next", List.of(), null);

        when(chapterRepository.existsById(1L)).thenReturn(true);
        when(problemRepository.existsByIdAndChapterId(100L, 1L)).thenReturn(true);
        when(getRandomProblemService.getRandomProblem(new GetRandomProblemCommand(1L, 1L))).thenReturn(nextProblem);

        GetRandomProblemResult result = skipProblemService.skipProblem(command);

        verify(solveAttemptRepository).saveSkippedProblem(1L, 1L, 100L);
        verify(getRandomProblemService).getRandomProblem(new GetRandomProblemCommand(1L, 1L));
        assertThat(result.problemId()).isEqualTo(101L);
    }
}
