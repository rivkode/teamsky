package com.project.quiz.application.statistics.service;

import com.project.quiz.application.statistics.repository.ProblemCorrectRateCacheRepository;
import com.project.quiz.application.statistics.repository.ProblemStatisticsQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProblemCorrectRateCacheRefreshService {

    private final ProblemStatisticsQueryRepository problemStatisticsQueryRepository;
    private final ProblemCorrectRateCacheRepository problemCorrectRateCacheRepository;

    @Transactional(readOnly = true)
    public void refreshAll() {
        problemStatisticsQueryRepository.findAllStatistics()
                .forEach(statistics -> problemCorrectRateCacheRepository.put(
                        statistics.problemId(),
                        statistics.correctRate()
                ));
    }
}
