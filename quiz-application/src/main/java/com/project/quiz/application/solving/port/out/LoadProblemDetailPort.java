package com.project.quiz.application.solving.port.out;

import com.project.quiz.domain.problem.Problem;

import java.util.Optional;

public interface LoadProblemDetailPort {

    Optional<Problem> loadById(Long problemId);
}
