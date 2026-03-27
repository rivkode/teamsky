package com.project.quiz.application.solving.port.out;

import com.project.quiz.domain.problem.Problem;

import java.util.List;

public interface PickRandomProblemPort {

    Problem pick(List<Problem> problems);
}
