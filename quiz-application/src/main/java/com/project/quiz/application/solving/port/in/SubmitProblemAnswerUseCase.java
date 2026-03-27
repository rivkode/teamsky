package com.project.quiz.application.solving.port.in;

public interface SubmitProblemAnswerUseCase {

    SubmitProblemAnswerResult submit(SubmitProblemAnswerCommand command);
}
