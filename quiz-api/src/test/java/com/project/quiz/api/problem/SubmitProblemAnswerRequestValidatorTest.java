package com.project.quiz.api.problem;

import com.project.quiz.domain.problem.ProblemAnswerFormat;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SubmitProblemAnswerRequestValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void objectiveRequestRejectsDuplicateChoices() {
        Set<ConstraintViolation<SubmitProblemAnswerRequest>> violations = validator.validate(
                new SubmitProblemAnswerRequest(1001L, 1L, ProblemAnswerFormat.OBJECTIVE, List.of(1, 1), null)
        );

        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("selectedChoices must not contain duplicates");
    }

    @Test
    void objectiveRequestRejectsSubjectiveAnswer() {
        Set<ConstraintViolation<SubmitProblemAnswerRequest>> violations = validator.validate(
                new SubmitProblemAnswerRequest(1001L, 1L, ProblemAnswerFormat.OBJECTIVE, List.of(1, 2), "text")
        );

        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("subjectiveAnswer must be blank for OBJECTIVE answerType");
    }

    @Test
    void subjectiveRequestRejectsSelectedChoices() {
        Set<ConstraintViolation<SubmitProblemAnswerRequest>> violations = validator.validate(
                new SubmitProblemAnswerRequest(1001L, 1L, ProblemAnswerFormat.SUBJECTIVE, List.of(1), "answer")
        );

        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("selectedChoices must be empty for SUBJECTIVE answerType");
    }
}
