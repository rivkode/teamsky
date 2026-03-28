package com.project.quiz.application.statistics.repository;

import java.util.Optional;

public interface ProblemCorrectRateCacheRepository {

    Optional<ProblemCorrectRateCacheEntry> find(Long problemId);

    void put(Long problemId, Integer correctRate);

    void evict(Long problemId);
}
