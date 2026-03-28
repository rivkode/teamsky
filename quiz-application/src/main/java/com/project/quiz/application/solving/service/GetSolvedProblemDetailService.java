package com.project.quiz.application.solving.service;

import com.project.quiz.application.solving.exception.ProblemNotFoundInChapterException;
import com.project.quiz.application.solving.exception.SolvedProblemNotFoundException;
import com.project.quiz.application.solving.model.GetSolvedProblemDetailCommand;
import com.project.quiz.application.solving.model.GetSolvedProblemDetailResult;
import com.project.quiz.application.solving.repository.ProblemRepository;
import com.project.quiz.application.solving.repository.SolveAttemptRepository;
import com.project.quiz.domain.problem.Problem;
import com.project.quiz.domain.problem.ProblemAnswerFormat;
import com.project.quiz.domain.solving.SolvedProblem;
import com.project.quiz.domain.statistics.ProblemCorrectRatePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GetSolvedProblemDetailService {

    private final ProblemRepository problemRepository;
    private final SolveAttemptRepository solveAttemptRepository;
    private final ProblemCorrectRatePolicy problemCorrectRatePolicy = new ProblemCorrectRatePolicy();

    public GetSolvedProblemDetailResult getDetail(GetSolvedProblemDetailCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        Problem problem = problemRepository.findById(command.problemId())
                .orElseThrow(() -> new ProblemNotFoundInChapterException(null, command.problemId()));

        SolvedProblem solvedProblem = solveAttemptRepository.findLatestSolvedProblem(command.userId(), command.problemId())
                .orElseThrow(() -> new SolvedProblemNotFoundException(command.userId(), command.problemId()));

        Integer correctRate = solveAttemptRepository.findCorrectRateByProblemId(command.problemId())
                .map(problemCorrectRatePolicy::calculateExposedRate)
                .orElse(null);

        return new GetSolvedProblemDetailResult(
                problem.id(),
                problem.answerFormat(),
                solvedProblem.answerStatus(),
                problem.explanation(),
                toAnswers(problem.answerFormat(), problem.answerKey().objectiveAnswers().stream().sorted().map(String::valueOf).toList(), problem.answerKey().subjectiveAnswers()),
                toAnswers(problem.answerFormat(), solvedProblem.userAnswer().selectedChoices() == null ? List.of() : solvedProblem.userAnswer().selectedChoices().stream().map(String::valueOf).toList(), solvedProblem.userAnswer().subjectiveAnswer() == null ? List.of() : List.of(solvedProblem.userAnswer().subjectiveAnswer())),
                correctRate
        );
    }

    private List<String> toAnswers(ProblemAnswerFormat answerFormat, List<String> objectiveAnswers, List<String> subjectiveAnswers) {
        return answerFormat == ProblemAnswerFormat.OBJECTIVE ? objectiveAnswers : subjectiveAnswers;
    }
}
