package com.project.quiz.infrastructure.statistics;

import com.project.quiz.application.statistics.repository.ProblemStatisticsCommandRepository;
import com.project.quiz.domain.statistics.ProblemStatistics;
import com.project.quiz.infrastructure.persistence.entity.ProblemStatisticsJpaEntity;
import com.project.quiz.infrastructure.persistence.repository.ProblemStatisticsJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProblemStatisticsCommandRepositoryImpl implements ProblemStatisticsCommandRepository {

    private final ProblemStatisticsJpaRepository problemStatisticsJpaRepository;

    @Override
    public Optional<ProblemStatistics> findStatistics(Long problemId) {
        return problemStatisticsJpaRepository.findById(problemId)
                .map(entity -> new ProblemStatistics(
                        entity.getProblemId(),
                        entity.getSolvedUserCount(),
                        entity.getCorrectUserCount(),
                        entity.getCorrectRate()
                ));
    }

    @Override
    public Optional<ProblemStatistics> findStatisticsForUpdate(Long problemId) {
        return problemStatisticsJpaRepository.findByProblemIdForUpdate(problemId)
                .map(entity -> new ProblemStatistics(
                        entity.getProblemId(),
                        entity.getSolvedUserCount(),
                        entity.getCorrectUserCount(),
                        entity.getCorrectRate()
                ));
    }

    @Override
    @Transactional
    public void save(ProblemStatistics problemStatistics) {
        ProblemStatisticsJpaEntity entity = problemStatisticsJpaRepository.findByProblemIdForUpdate(problemStatistics.problemId())
                .orElseThrow(() -> new IllegalArgumentException("Problem statistics not found. problemId=" + problemStatistics.problemId()));

        entity.update(
                problemStatistics.solvedUserCount(),
                problemStatistics.correctUserCount(),
                problemStatistics.correctRate(),
                LocalDateTime.now()
        );
    }
}
