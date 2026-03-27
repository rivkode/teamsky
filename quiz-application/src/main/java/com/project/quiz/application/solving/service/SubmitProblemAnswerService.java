package com.project.quiz.application.solving.service;

import com.project.quiz.application.solving.exception.ProblemAnswerTypeMismatchException;
import com.project.quiz.application.solving.exception.ProblemNotFoundInChapterException;
import com.project.quiz.application.solving.port.in.SubmitProblemAnswerCommand;
import com.project.quiz.application.solving.port.in.SubmitProblemAnswerResult;
import com.project.quiz.application.solving.port.in.SubmitProblemAnswerUseCase;
import com.project.quiz.application.solving.port.out.LoadProblemDetailPort;
import com.project.quiz.application.solving.port.out.SaveSolvedAttemptPort;
import com.project.quiz.domain.problem.Problem;
import com.project.quiz.domain.solving.GradingResult;
import com.project.quiz.domain.solving.SubmittedAnswer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SubmitProblemAnswerService implements SubmitProblemAnswerUseCase {

    private final LoadProblemDetailPort loadProblemDetailPort;
    private final SaveSolvedAttemptPort saveSolvedAttemptPort;

    @Override
    @Transactional
    public SubmitProblemAnswerResult submit(SubmitProblemAnswerCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        Problem problem = loadProblemDetailPort.loadById(command.problemId())
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

        saveSolvedAttemptPort.saveSolvedAttempt(
                command.userId(),
                problem.chapterId(),
                problem.id(),
                submittedAnswer,
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
