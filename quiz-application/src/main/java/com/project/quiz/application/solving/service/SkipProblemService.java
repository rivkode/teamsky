package com.project.quiz.application.solving.service;

import com.project.quiz.application.solving.exception.ChapterNotFoundException;
import com.project.quiz.application.solving.exception.ProblemNotFoundInChapterException;
import com.project.quiz.application.solving.port.in.GetRandomProblemCommand;
import com.project.quiz.application.solving.port.in.GetRandomProblemResult;
import com.project.quiz.application.solving.port.in.GetRandomProblemUseCase;
import com.project.quiz.application.solving.port.in.SkipProblemCommand;
import com.project.quiz.application.solving.port.in.SkipProblemUseCase;
import com.project.quiz.application.solving.port.out.CheckChapterExistsPort;
import com.project.quiz.application.solving.port.out.CheckProblemInChapterPort;
import com.project.quiz.application.solving.port.out.SaveSkippedProblemPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SkipProblemService implements SkipProblemUseCase {

    private final CheckChapterExistsPort checkChapterExistsPort;
    private final CheckProblemInChapterPort checkProblemInChapterPort;
    private final SaveSkippedProblemPort saveSkippedProblemPort;
    private final GetRandomProblemUseCase getRandomProblemUseCase;

    @Override
    @Transactional
    public GetRandomProblemResult skipProblem(SkipProblemCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        if (!checkChapterExistsPort.existsById(command.chapterId())) {
            throw new ChapterNotFoundException(command.chapterId());
        }

        if (!checkProblemInChapterPort.existsByIdAndChapterId(command.problemId(), command.chapterId())) {
            throw new ProblemNotFoundInChapterException(command.chapterId(), command.problemId());
        }

        saveSkippedProblemPort.saveSkippedProblem(command.userId(), command.chapterId(), command.problemId());

        return getRandomProblemUseCase.getRandomProblem(
                new GetRandomProblemCommand(command.userId(), command.chapterId())
        );
    }
}
