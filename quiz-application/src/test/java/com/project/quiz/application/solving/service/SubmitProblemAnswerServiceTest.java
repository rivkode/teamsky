package com.project.quiz.application.solving.service;

import com.project.quiz.application.statistics.service.ProblemStatisticsUpdateService;
import com.project.quiz.application.solving.exception.InvalidProblemChoiceException;
import com.project.quiz.application.solving.exception.ProblemAnswerTypeMismatchException;
import com.project.quiz.application.solving.model.SubmitProblemAnswerCommand;
import com.project.quiz.application.solving.model.SubmitProblemAnswerResult;
import com.project.quiz.application.solving.repository.ProblemRepository;
import com.project.quiz.application.solving.repository.SolveAttemptRepository;
import com.project.quiz.domain.problem.Problem;
import com.project.quiz.domain.problem.ProblemAnswerFormat;
import com.project.quiz.domain.problem.ProblemAnswerKey;
import com.project.quiz.domain.problem.ProblemChoice;
import com.project.quiz.domain.problem.ProblemType;
import com.project.quiz.domain.solving.AnswerStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmitProblemAnswerServiceTest {

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private SolveAttemptRepository solveAttemptRepository;

    @Mock
    private ProblemStatisticsUpdateService problemStatisticsUpdateService;

    private SubmitProblemAnswerService submitProblemAnswerService;

    @BeforeEach
    void setUp() {
        submitProblemAnswerService = new SubmitProblemAnswerService(
                problemRepository,
                solveAttemptRepository,
                problemStatisticsUpdateService
        );
    }

    @Test
    void objectiveCorrectAnswerReturnsCorrect() {
        Problem problem = new Problem(
                100L, 1L, "problem", ProblemAnswerFormat.OBJECTIVE, ProblemType.MULTIPLE_ANSWER,
                List.of(new ProblemChoice(1, "a"), new ProblemChoice(2, "b")),
                new ProblemAnswerKey(Set.of(1, 2), List.of()),
                "해설"
        );
        when(problemRepository.findById(100L)).thenReturn(Optional.of(problem));

        SubmitProblemAnswerResult result = submitProblemAnswerService.submit(
                new SubmitProblemAnswerCommand(100L, 1L, ProblemAnswerFormat.OBJECTIVE, List.of(1, 2), null)
        );

        assertThat(result.answerStatus()).isEqualTo(AnswerStatus.CORRECT);
        assertThat(result.problemAnswers()).containsExactly("1", "2");
        verify(solveAttemptRepository).saveSolvedAttempt(1L, 1L, 100L,
                new com.project.quiz.domain.solving.SubmittedAnswer(ProblemAnswerFormat.OBJECTIVE, List.of(1, 2), null),
                AnswerStatus.CORRECT);
        verify(problemStatisticsUpdateService).recordSolvedProblem(100L, 1L, AnswerStatus.CORRECT);
    }

    @Test
    void objectivePartialAnswerReturnsPartial() {
        Problem problem = new Problem(
                100L, 1L, "problem", ProblemAnswerFormat.OBJECTIVE, ProblemType.MULTIPLE_ANSWER,
                List.of(new ProblemChoice(1, "a"), new ProblemChoice(2, "b"), new ProblemChoice(3, "c")),
                new ProblemAnswerKey(Set.of(1, 2), List.of()), "해설"
        );
        when(problemRepository.findById(100L)).thenReturn(Optional.of(problem));

        SubmitProblemAnswerResult result = submitProblemAnswerService.submit(
                new SubmitProblemAnswerCommand(100L, 1L, ProblemAnswerFormat.OBJECTIVE, List.of(1, 3), null)
        );

        assertThat(result.answerStatus()).isEqualTo(AnswerStatus.PARTIAL);
    }

    @Test
    void subjectiveCorrectAnswerReturnsCorrect() {
        Problem problem = new Problem(
                200L, 1L, "problem", ProblemAnswerFormat.SUBJECTIVE, ProblemType.SINGLE_ANSWER,
                List.of(), new ProblemAnswerKey(Set.of(), List.of("싱글톤", "singleton")), "해설"
        );
        when(problemRepository.findById(200L)).thenReturn(Optional.of(problem));

        SubmitProblemAnswerResult result = submitProblemAnswerService.submit(
                new SubmitProblemAnswerCommand(200L, 1L, ProblemAnswerFormat.SUBJECTIVE, null, " singleton ")
        );

        assertThat(result.answerStatus()).isEqualTo(AnswerStatus.CORRECT);
    }

    @Test
    void answerTypeMismatchThrowsException() {
        Problem problem = new Problem(
                200L, 1L, "problem", ProblemAnswerFormat.SUBJECTIVE, ProblemType.SINGLE_ANSWER,
                List.of(), new ProblemAnswerKey(Set.of(), List.of("싱글톤")), "해설"
        );
        when(problemRepository.findById(200L)).thenReturn(Optional.of(problem));

        assertThatThrownBy(() -> submitProblemAnswerService.submit(
                new SubmitProblemAnswerCommand(200L, 1L, ProblemAnswerFormat.OBJECTIVE, List.of(1), null)
        )).isInstanceOf(ProblemAnswerTypeMismatchException.class);
    }

    @Test
    void invalidObjectiveChoiceThrowsException() {
        Problem problem = new Problem(
                100L, 1L, "problem", ProblemAnswerFormat.OBJECTIVE, ProblemType.MULTIPLE_ANSWER,
                List.of(new ProblemChoice(1, "a"), new ProblemChoice(2, "b")),
                new ProblemAnswerKey(Set.of(1, 2), List.of()),
                "해설"
        );
        when(problemRepository.findById(100L)).thenReturn(Optional.of(problem));

        assertThatThrownBy(() -> submitProblemAnswerService.submit(
                new SubmitProblemAnswerCommand(100L, 1L, ProblemAnswerFormat.OBJECTIVE, List.of(1, 3), null)
        )).isInstanceOf(InvalidProblemChoiceException.class);

        verify(solveAttemptRepository, never()).saveSolvedAttempt(
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
        );
        verify(problemStatisticsUpdateService, never()).recordSolvedProblem(
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.any()
        );
    }
}
