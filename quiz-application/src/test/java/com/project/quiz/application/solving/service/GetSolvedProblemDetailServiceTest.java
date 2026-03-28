package com.project.quiz.application.solving.service;

import com.project.quiz.application.statistics.service.ProblemCorrectRateService;
import com.project.quiz.application.solving.exception.SolvedProblemNotFoundException;
import com.project.quiz.application.solving.model.GetSolvedProblemDetailCommand;
import com.project.quiz.application.solving.model.GetSolvedProblemDetailResult;
import com.project.quiz.application.solving.repository.ProblemRepository;
import com.project.quiz.application.solving.repository.SolveAttemptRepository;
import com.project.quiz.domain.problem.Problem;
import com.project.quiz.domain.problem.ProblemAnswerFormat;
import com.project.quiz.domain.problem.ProblemAnswerKey;
import com.project.quiz.domain.problem.ProblemType;
import com.project.quiz.domain.solving.AnswerStatus;
import com.project.quiz.domain.solving.SolvedProblem;
import com.project.quiz.domain.solving.SubmittedAnswer;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSolvedProblemDetailServiceTest {

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private SolveAttemptRepository solveAttemptRepository;

    @Mock
    private ProblemCorrectRateService problemCorrectRateService;

    private GetSolvedProblemDetailService getSolvedProblemDetailService;

    @BeforeEach
    void setUp() {
        getSolvedProblemDetailService = new GetSolvedProblemDetailService(
                problemRepository,
                solveAttemptRepository,
                problemCorrectRateService
        );
    }

    @Test
    void returnSolvedProblemDetail() {
        Problem problem = new Problem(
                3L,
                1L,
                "problem",
                ProblemAnswerFormat.OBJECTIVE,
                ProblemType.MULTIPLE_ANSWER,
                List.of(),
                new ProblemAnswerKey(Set.of(1, 2), List.of()),
                "해설"
        );

        when(problemRepository.findById(3L)).thenReturn(Optional.of(problem));
        when(solveAttemptRepository.findLatestSolvedProblem(1L, 3L))
                .thenReturn(Optional.of(new SolvedProblem(
                        3L,
                        AnswerStatus.PARTIAL,
                        new SubmittedAnswer(ProblemAnswerFormat.OBJECTIVE, List.of(1, 3), null)
                )));
        when(problemCorrectRateService.getCorrectRate(3L)).thenReturn(67);

        GetSolvedProblemDetailResult result = getSolvedProblemDetailService.getDetail(
                new GetSolvedProblemDetailCommand(1L, 3L)
        );

        assertThat(result.problemId()).isEqualTo(3L);
        assertThat(result.answerStatus()).isEqualTo(AnswerStatus.PARTIAL);
        assertThat(result.problemAnswers()).containsExactly("1", "2");
        assertThat(result.userAnswers()).containsExactly("1", "3");
        assertThat(result.answerCorrectRate()).isEqualTo(67);
    }

    @Test
    void throwWhenSolvedProblemMissing() {
        Problem problem = new Problem(
                3L,
                1L,
                "problem",
                ProblemAnswerFormat.OBJECTIVE,
                ProblemType.SINGLE_ANSWER,
                List.of(),
                new ProblemAnswerKey(Set.of(1), List.of()),
                "해설"
        );

        when(problemRepository.findById(3L)).thenReturn(Optional.of(problem));
        when(solveAttemptRepository.findLatestSolvedProblem(1L, 3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> getSolvedProblemDetailService.getDetail(new GetSolvedProblemDetailCommand(1L, 3L)))
                .isInstanceOf(SolvedProblemNotFoundException.class);
    }
}
