package com.project.quiz.application.solving.service;

import com.project.quiz.application.solving.exception.ChapterNotFoundException;
import com.project.quiz.application.solving.exception.NoAvailableProblemException;
import com.project.quiz.application.solving.model.GetRandomProblemChoiceResult;
import com.project.quiz.application.solving.model.GetRandomProblemCommand;
import com.project.quiz.application.solving.model.GetRandomProblemResult;
import com.project.quiz.application.solving.repository.ChapterRepository;
import com.project.quiz.application.solving.repository.ProblemRepository;
import com.project.quiz.application.solving.repository.SolveAttemptRepository;
import com.project.quiz.domain.problem.Problem;
import com.project.quiz.domain.problem.ProblemChoice;
import com.project.quiz.domain.solving.UserChapterSolvingState;
import com.project.quiz.domain.statistics.ProblemCorrectRatePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class GetRandomProblemService {

    private final ChapterRepository chapterRepository;
    private final ProblemRepository problemRepository;
    private final SolveAttemptRepository solveAttemptRepository;
    private final ProblemCorrectRatePolicy problemCorrectRatePolicy = new ProblemCorrectRatePolicy();

    public GetRandomProblemResult getRandomProblem(GetRandomProblemCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        if (!chapterRepository.existsById(command.chapterId())) {
            throw new ChapterNotFoundException(command.chapterId());
        }

        UserChapterSolvingState solvingState =
                solveAttemptRepository.findUserChapterSolvingState(command.userId(), command.chapterId());

        List<Problem> selectableProblems = problemRepository.findAllByChapterId(command.chapterId()).stream()
                .filter(solvingState::isSelectable)
                .toList();

        if (selectableProblems.isEmpty()) {
            throw new NoAvailableProblemException(command.userId(), command.chapterId());
        }

        Problem selectedProblem = pickRandomProblem(selectableProblems);
        Integer correctRate = solveAttemptRepository.findCorrectRateByProblemId(selectedProblem.id())
                .map(problemCorrectRatePolicy::calculateExposedRate)
                .orElse(null);

        return new GetRandomProblemResult(
                selectedProblem.id(),
                selectedProblem.content(),
                selectedProblem.choices().stream()
                        .map(this::toChoiceResult)
                        .toList(),
                correctRate
        );
    }

    private GetRandomProblemChoiceResult toChoiceResult(ProblemChoice choice) {
        return new GetRandomProblemChoiceResult(choice.sequence(), choice.content());
    }

    private Problem pickRandomProblem(List<Problem> selectableProblems) {
        if (selectableProblems.size() == 1) {
            return selectableProblems.get(0);
        }
        return selectableProblems.get(ThreadLocalRandom.current().nextInt(selectableProblems.size()));
    }
}
