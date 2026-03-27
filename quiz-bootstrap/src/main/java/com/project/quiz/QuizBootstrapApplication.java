package com.project.quiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.project")
public class QuizBootstrapApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizBootstrapApplication.class, args);
    }
}
