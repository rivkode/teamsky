package com.project.quiz.infrastructure.statistics;

import com.project.quiz.application.statistics.repository.ProblemStatisticsQueryRepository;
import com.project.quiz.domain.statistics.ProblemStatistics;
import com.project.quiz.infrastructure.persistence.repository.ProblemStatisticsJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProblemStatisticsQueryRepositoryImpl implements ProblemStatisticsQueryRepository {

    private final ProblemStatisticsJpaRepository problemStatisticsJpaRepository;

    @Override
    public List<ProblemStatistics> findAllStatistics() {
        return problemStatisticsJpaRepository.findAll().stream()
                .map(entity -> new ProblemStatistics(
                        entity.getProblemId(),
                        entity.getSolvedUserCount(),
                        entity.getCorrectUserCount(),
                        entity.getCorrectRate()
                ))
                .toList();
    }
}
