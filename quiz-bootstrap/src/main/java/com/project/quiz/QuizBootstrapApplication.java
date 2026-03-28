package com.project.quiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.project")
@EnableScheduling
public class QuizBootstrapApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizBootstrapApplication.class, args);
    }
}
