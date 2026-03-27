package com.project.quiz.application.solving.port.in;

public interface GetRandomProblemUseCase {

    GetRandomProblemResult getRandomProblem(GetRandomProblemCommand command);
}
