package com.project.quiz.application.statistics.service;

import com.project.quiz.application.statistics.repository.ProblemCorrectRateCacheEntry;
import com.project.quiz.application.statistics.repository.ProblemCorrectRateCacheRepository;
import com.project.quiz.application.statistics.repository.ProblemCorrectRateQueryRepository;
import com.project.quiz.domain.statistics.ProblemCorrectRate;
import com.project.quiz.domain.statistics.ProblemCorrectRatePolicy;
import com.project.quiz.domain.statistics.ProblemCorrectRateSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProblemCorrectRateService {

    private final ProblemCorrectRateQueryRepository problemCorrectRateQueryRepository;
    private final ProblemCorrectRateCacheRepository problemCorrectRateCacheRepository;
    private final ProblemCorrectRatePolicy problemCorrectRatePolicy;

    public Integer getCorrectRate(Long problemId) {
        Optional<ProblemCorrectRateCacheEntry> cachedCorrectRate = problemCorrectRateCacheRepository.find(problemId);
        if (cachedCorrectRate.isPresent()) {
            return cachedCorrectRate.get().correctRate();
        }

        Integer calculatedCorrectRate = problemCorrectRateQueryRepository.findCorrectRateSummary(problemId)
                .flatMap(problemCorrectRatePolicy::calculate)
                .map(ProblemCorrectRate::value)
                .orElse(null);

        problemCorrectRateCacheRepository.put(problemId, calculatedCorrectRate);
        return calculatedCorrectRate;
    }
}
