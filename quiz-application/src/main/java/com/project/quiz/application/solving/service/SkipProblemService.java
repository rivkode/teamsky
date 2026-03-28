package com.project.quiz.application.solving.service;

import com.project.quiz.application.solving.exception.ChapterNotFoundException;
import com.project.quiz.application.solving.exception.ProblemNotFoundInChapterException;
import com.project.quiz.application.solving.model.GetRandomProblemCommand;
import com.project.quiz.application.solving.model.GetRandomProblemResult;
import com.project.quiz.application.solving.model.SkipProblemCommand;
import com.project.quiz.application.solving.repository.ChapterRepository;
import com.project.quiz.application.solving.repository.ProblemRepository;
import com.project.quiz.application.solving.repository.SolveAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SkipProblemService {

    private final ChapterRepository chapterRepository;
    private final ProblemRepository problemRepository;
    private final SolveAttemptRepository solveAttemptRepository;
    private final GetRandomProblemService getRandomProblemService;

    @Transactional
    public GetRandomProblemResult skipProblem(SkipProblemCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        if (!chapterRepository.existsById(command.chapterId())) {
            throw new ChapterNotFoundException(command.chapterId());
        }

        if (!problemRepository.existsByIdAndChapterId(command.problemId(), command.chapterId())) {
            throw new ProblemNotFoundInChapterException(command.chapterId(), command.problemId());
        }

        solveAttemptRepository.saveSkippedProblem(command.userId(), command.chapterId(), command.problemId());

        return getRandomProblemService.getRandomProblem(
                new GetRandomProblemCommand(command.userId(), command.chapterId())
        );
    }
}
