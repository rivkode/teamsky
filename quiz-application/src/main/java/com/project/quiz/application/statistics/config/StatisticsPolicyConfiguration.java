package com.project.quiz.application.statistics.config;

import com.project.quiz.domain.statistics.ProblemCorrectRatePolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatisticsPolicyConfiguration {

    @Bean
    public ProblemCorrectRatePolicy problemCorrectRatePolicy() {
        return new ProblemCorrectRatePolicy();
    }
}
