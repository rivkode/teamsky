package com.project.quiz.application.solving.service;

import com.project.quiz.application.solving.port.in.GetRandomProblemChoiceResult;
import com.project.quiz.application.solving.port.in.GetRandomProblemCommand;
import com.project.quiz.application.solving.port.in.GetRandomProblemResult;
import com.project.quiz.application.solving.port.in.GetRandomProblemUseCase;
import com.project.quiz.application.solving.port.out.CheckChapterExistsPort;
import com.project.quiz.application.solving.port.out.LoadChapterProblemsPort;
import com.project.quiz.application.solving.port.out.LoadProblemCorrectRatePort;
import com.project.quiz.application.solving.port.out.LoadUserChapterSolvingStatePort;
import com.project.quiz.application.solving.port.out.PickRandomProblemPort;
import com.project.quiz.application.solving.exception.ChapterNotFoundException;
import com.project.quiz.application.solving.exception.NoAvailableProblemException;
import com.project.quiz.domain.problem.Problem;
import com.project.quiz.domain.problem.ProblemChoice;
import com.project.quiz.domain.solving.UserChapterSolvingState;
import com.project.quiz.domain.statistics.ProblemCorrectRatePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GetRandomProblemService implements GetRandomProblemUseCase {

    private final CheckChapterExistsPort checkChapterExistsPort;
    private final LoadChapterProblemsPort loadChapterProblemsPort;
    private final LoadUserChapterSolvingStatePort loadUserChapterSolvingStatePort;
    private final LoadProblemCorrectRatePort loadProblemCorrectRatePort;
    private final PickRandomProblemPort pickRandomProblemPort;
    private final ProblemCorrectRatePolicy problemCorrectRatePolicy = new ProblemCorrectRatePolicy();

    @Override
    public GetRandomProblemResult getRandomProblem(GetRandomProblemCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        if (!checkChapterExistsPort.existsById(command.chapterId())) {
            throw new ChapterNotFoundException(command.chapterId());
        }

        UserChapterSolvingState solvingState =
                loadUserChapterSolvingStatePort.load(command.userId(), command.chapterId());

        List<Problem> selectableProblems = loadChapterProblemsPort.loadByChapterId(command.chapterId()).stream()
                .filter(solvingState::isSelectable)
                .toList();

        if (selectableProblems.isEmpty()) {
            throw new NoAvailableProblemException(command.userId(), command.chapterId());
        }

        Problem selectedProblem = pickRandomProblemPort.pick(selectableProblems);
        Integer correctRate = loadProblemCorrectRatePort.loadByProblemId(selectedProblem.id())
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
}
