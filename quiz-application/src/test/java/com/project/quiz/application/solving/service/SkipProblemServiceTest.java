package com.project.quiz.application.solving.service;

import com.project.quiz.application.solving.exception.ChapterNotFoundException;
import com.project.quiz.application.solving.exception.ProblemNotFoundInChapterException;
import com.project.quiz.application.solving.port.in.GetRandomProblemCommand;
import com.project.quiz.application.solving.port.in.GetRandomProblemResult;
import com.project.quiz.application.solving.port.in.GetRandomProblemUseCase;
import com.project.quiz.application.solving.port.in.SkipProblemCommand;
import com.project.quiz.application.solving.port.out.CheckChapterExistsPort;
import com.project.quiz.application.solving.port.out.CheckProblemInChapterPort;
import com.project.quiz.application.solving.port.out.SaveSkippedProblemPort;
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
    private CheckChapterExistsPort checkChapterExistsPort;

    @Mock
    private CheckProblemInChapterPort checkProblemInChapterPort;

    @Mock
    private SaveSkippedProblemPort saveSkippedProblemPort;

    @Mock
    private GetRandomProblemUseCase getRandomProblemUseCase;

    private SkipProblemService skipProblemService;

    @BeforeEach
    void setUp() {
        skipProblemService = new SkipProblemService(
                checkChapterExistsPort,
                checkProblemInChapterPort,
                saveSkippedProblemPort,
                getRandomProblemUseCase
        );
    }

    @Test
    void chapterDoesNotExistThenThrowException() {
        SkipProblemCommand command = new SkipProblemCommand(1L, 99L, 100L);

        when(checkChapterExistsPort.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> skipProblemService.skipProblem(command))
                .isInstanceOf(ChapterNotFoundException.class);
    }

    @Test
    void problemDoesNotBelongToChapterThenThrowException() {
        SkipProblemCommand command = new SkipProblemCommand(1L, 1L, 100L);

        when(checkChapterExistsPort.existsById(1L)).thenReturn(true);
        when(checkProblemInChapterPort.existsByIdAndChapterId(100L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> skipProblemService.skipProblem(command))
                .isInstanceOf(ProblemNotFoundInChapterException.class);
    }

    @Test
    void saveSkipAndReturnNextRandomProblem() {
        SkipProblemCommand command = new SkipProblemCommand(1L, 1L, 100L);
        GetRandomProblemResult nextProblem = new GetRandomProblemResult(101L, "next", List.of(), null);

        when(checkChapterExistsPort.existsById(1L)).thenReturn(true);
        when(checkProblemInChapterPort.existsByIdAndChapterId(100L, 1L)).thenReturn(true);
        when(getRandomProblemUseCase.getRandomProblem(new GetRandomProblemCommand(1L, 1L))).thenReturn(nextProblem);

        GetRandomProblemResult result = skipProblemService.skipProblem(command);

        verify(saveSkippedProblemPort).saveSkippedProblem(1L, 1L, 100L);
        verify(getRandomProblemUseCase).getRandomProblem(new GetRandomProblemCommand(1L, 1L));
        assertThat(result.problemId()).isEqualTo(101L);
    }
}
