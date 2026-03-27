package com.project.quiz.infrastructure.persistence.repository;

public interface ProblemCorrectRateProjection {

    long getSolvedUserCount();

    long getCorrectUserCount();
}
