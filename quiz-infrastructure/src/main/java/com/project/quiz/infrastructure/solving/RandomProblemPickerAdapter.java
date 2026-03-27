package com.project.quiz.infrastructure.solving;

import com.project.quiz.application.solving.port.out.PickRandomProblemPort;
import com.project.quiz.domain.problem.Problem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomProblemPickerAdapter implements PickRandomProblemPort {

    @Override
    public Problem pick(List<Problem> problems) {
        int index = ThreadLocalRandom.current().nextInt(problems.size());
        return problems.get(index);
    }
}
