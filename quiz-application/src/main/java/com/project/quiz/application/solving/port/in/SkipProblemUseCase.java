package com.project.quiz.application.solving.port.in;

public interface SkipProblemUseCase {

    GetRandomProblemResult skipProblem(SkipProblemCommand command);
}
