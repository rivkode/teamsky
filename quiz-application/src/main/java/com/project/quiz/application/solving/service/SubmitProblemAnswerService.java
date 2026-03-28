package com.project.quiz.application.solving.service;

import com.project.quiz.application.statistics.service.ProblemStatisticsUpdateService;
import com.project.quiz.application.solving.exception.ProblemAnswerTypeMismatchException;
import com.project.quiz.application.solving.exception.ProblemNotFoundInChapterException;
import com.project.quiz.application.solving.model.SubmitProblemAnswerCommand;
import com.project.quiz.application.solving.model.SubmitProblemAnswerResult;
import com.project.quiz.application.solving.repository.ProblemRepository;
import com.project.quiz.application.solving.repository.SolveAttemptRepository;
import com.project.quiz.domain.problem.Problem;
import com.project.quiz.domain.solving.GradingResult;
import com.project.quiz.domain.solving.SubmittedAnswer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SubmitProblemAnswerService {

    private final ProblemRepository problemRepository;
    private final SolveAttemptRepository solveAttemptRepository;
    private final ProblemStatisticsUpdateService problemStatisticsUpdateService;

    @Transactional
    public SubmitProblemAnswerResult submit(SubmitProblemAnswerCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        Problem problem = problemRepository.findById(command.problemId())
                .orElseThrow(() -> new ProblemNotFoundInChapterException(null, command.problemId()));

        if (problem.answerFormat() != command.answerType()) {
            throw new ProblemAnswerTypeMismatchException(command.problemId());
        }

        SubmittedAnswer submittedAnswer = new SubmittedAnswer(
                command.answerType(),
                command.selectedChoices(),
                command.subjectiveAnswer()
        );

        GradingResult gradingResult = problem.grade(submittedAnswer);

        solveAttemptRepository.saveSolvedAttempt(
                command.userId(),
                problem.chapterId(),
                problem.id(),
                submittedAnswer,
                gradingResult.answerStatus()
        );
        problemStatisticsUpdateService.recordSolvedProblem(
                problem.id(),
                command.userId(),
                gradingResult.answerStatus()
        );

        return new SubmitProblemAnswerResult(
                gradingResult.problemId(),
                gradingResult.answerFormat(),
                gradingResult.answerStatus(),
                gradingResult.explanation(),
                gradingResult.problemAnswers()
        );
    }
}
