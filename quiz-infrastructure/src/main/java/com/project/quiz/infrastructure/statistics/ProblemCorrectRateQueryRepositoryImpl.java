package com.project.quiz.infrastructure.statistics;

import com.project.quiz.application.statistics.repository.ProblemCorrectRateQueryRepository;
import com.project.quiz.domain.statistics.ProblemCorrectRateSummary;
import com.project.quiz.infrastructure.persistence.repository.ProblemStatisticsJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProblemCorrectRateQueryRepositoryImpl implements ProblemCorrectRateQueryRepository {

    private final ProblemStatisticsJpaRepository problemStatisticsJpaRepository;

    @Override
    public Optional<ProblemCorrectRateSummary> findCorrectRateSummary(Long problemId) {
        return problemStatisticsJpaRepository.findById(problemId)
                .map(entity -> new ProblemCorrectRateSummary(
                        entity.getSolvedUserCount(),
                        entity.getCorrectUserCount()
                ));
    }
}
