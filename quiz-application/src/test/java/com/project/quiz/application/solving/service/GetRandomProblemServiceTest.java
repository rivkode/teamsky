package com.project.quiz.application.solving.service;

import com.project.quiz.application.statistics.service.ProblemCorrectRateService;
import com.project.quiz.application.solving.exception.ChapterNotFoundException;
import com.project.quiz.application.solving.exception.NoAvailableProblemException;
import com.project.quiz.application.solving.model.GetRandomProblemCommand;
import com.project.quiz.application.solving.model.GetRandomProblemResult;
import com.project.quiz.application.solving.repository.ChapterRepository;
import com.project.quiz.application.solving.repository.ProblemRepository;
import com.project.quiz.application.solving.repository.SolveAttemptRepository;
import com.project.quiz.domain.problem.Problem;
import com.project.quiz.domain.problem.ProblemAnswerFormat;
import com.project.quiz.domain.problem.ProblemAnswerKey;
import com.project.quiz.domain.problem.ProblemChoice;
import com.project.quiz.domain.problem.ProblemType;
import com.project.quiz.domain.solving.UserChapterSolvingState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetRandomProblemServiceTest {

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private SolveAttemptRepository solveAttemptRepository;

    @Mock
    private ProblemCorrectRateService problemCorrectRateService;

    private GetRandomProblemService getRandomProblemService;

    @BeforeEach
    void setUp() {
        getRandomProblemService = new GetRandomProblemService(
                chapterRepository,
                problemRepository,
                solveAttemptRepository,
                problemCorrectRateService
        );
    }

    @Test
    void chapterDoesNotExistThenThrowException() {
        GetRandomProblemCommand command = new GetRandomProblemCommand(1L, 100L);

        when(chapterRepository.existsById(100L)).thenReturn(false);

        assertThatThrownBy(() -> getRandomProblemService.getRandomProblem(command))
                .isInstanceOf(ChapterNotFoundException.class)
                .hasMessageContaining("chapterId=100");
    }

    @Test
    void returnRandomSelectableProblemAndRoundedCorrectRate() {
        GetRandomProblemCommand command = new GetRandomProblemCommand(1L, 10L);
        Problem solvedProblem = problem(101L, 10L, "solved");
        Problem skippedProblem = problem(102L, 10L, "skipped");
        Problem selectableProblem = problem(103L, 10L, "selectable");

        when(chapterRepository.existsById(10L)).thenReturn(true);
        when(solveAttemptRepository.findUserChapterSolvingState(1L, 10L))
                .thenReturn(new UserChapterSolvingState(1L, 10L, Set.of(101L), 102L));
        when(problemRepository.findAllByChapterId(10L))
                .thenReturn(List.of(solvedProblem, skippedProblem, selectableProblem));
        when(problemCorrectRateService.getCorrectRate(103L)).thenReturn(67);

        GetRandomProblemResult result = getRandomProblemService.getRandomProblem(command);

        assertThat(result.problemId()).isEqualTo(103L);
        assertThat(result.content()).isEqualTo("selectable");
        assertThat(result.answerCorrectRate()).isEqualTo(67);
        assertThat(result.choices()).hasSize(5);
        assertThat(result.choices().get(0).sequence()).isEqualTo(1);
    }

    @Test
    void whenAllProblemsAreUnavailableThenThrowException() {
        GetRandomProblemCommand command = new GetRandomProblemCommand(1L, 10L);
        Problem solvedProblem = problem(101L, 10L, "solved");
        Problem skippedProblem = problem(102L, 10L, "skipped");

        when(chapterRepository.existsById(10L)).thenReturn(true);
        when(solveAttemptRepository.findUserChapterSolvingState(1L, 10L))
                .thenReturn(new UserChapterSolvingState(1L, 10L, Set.of(101L), 102L));
        when(problemRepository.findAllByChapterId(10L))
                .thenReturn(List.of(solvedProblem, skippedProblem));

        assertThatThrownBy(() -> getRandomProblemService.getRandomProblem(command))
                .isInstanceOf(NoAvailableProblemException.class)
                .hasMessageContaining("userId=1")
                .hasMessageContaining("chapterId=10");
    }

    @Test
    void whenCorrectRateDoesNotMeetExposureConditionThenReturnNull() {
        GetRandomProblemCommand command = new GetRandomProblemCommand(1L, 10L);
        Problem selectableProblem = problem(103L, 10L, "selectable");

        when(chapterRepository.existsById(10L)).thenReturn(true);
        when(solveAttemptRepository.findUserChapterSolvingState(1L, 10L))
                .thenReturn(new UserChapterSolvingState(1L, 10L, Set.of(), null));
        when(problemRepository.findAllByChapterId(10L))
                .thenReturn(List.of(selectableProblem));
        when(problemCorrectRateService.getCorrectRate(103L)).thenReturn(null);

        GetRandomProblemResult result = getRandomProblemService.getRandomProblem(command);

        assertThat(result.answerCorrectRate()).isNull();
    }

    private Problem problem(Long problemId, Long chapterId, String content) {
        return new Problem(
                problemId,
                chapterId,
                content,
                ProblemAnswerFormat.OBJECTIVE,
                ProblemType.SINGLE_ANSWER,
                List.of(
                        new ProblemChoice(1, "choice-1"),
                        new ProblemChoice(2, "choice-2"),
                        new ProblemChoice(3, "choice-3"),
                        new ProblemChoice(4, "choice-4"),
                        new ProblemChoice(5, "choice-5")
                ),
                new ProblemAnswerKey(Set.of(1), List.of()),
                "해설"
        );
    }
}
