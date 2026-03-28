package com.project.quiz.api.problem;

import com.project.quiz.domain.problem.ProblemAnswerFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;

public class SubmitProblemAnswerRequestValidator implements ConstraintValidator<ValidSubmitProblemAnswerRequest, SubmitProblemAnswerRequest> {

    @Override
    public boolean isValid(SubmitProblemAnswerRequest value, ConstraintValidatorContext context) {
        if (value == null || value.answerType() == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        if (value.answerType() == ProblemAnswerFormat.OBJECTIVE) {
            if (value.selectedChoices() == null || value.selectedChoices().isEmpty()) {
                context.buildConstraintViolationWithTemplate("selectedChoices must not be empty for OBJECTIVE answerType")
                        .addPropertyNode("selectedChoices")
                        .addConstraintViolation();
                return false;
            }

            if (value.subjectiveAnswer() != null && !value.subjectiveAnswer().isBlank()) {
                context.buildConstraintViolationWithTemplate("subjectiveAnswer must be blank for OBJECTIVE answerType")
                        .addPropertyNode("subjectiveAnswer")
                        .addConstraintViolation();
                return false;
            }

            if (value.selectedChoices().size() != new HashSet<>(value.selectedChoices()).size()) {
                context.buildConstraintViolationWithTemplate("selectedChoices must not contain duplicates")
                        .addPropertyNode("selectedChoices")
                        .addConstraintViolation();
                return false;
            }

            return true;
        }

        if (value.selectedChoices() != null && !value.selectedChoices().isEmpty()) {
            context.buildConstraintViolationWithTemplate("selectedChoices must be empty for SUBJECTIVE answerType")
                    .addPropertyNode("selectedChoices")
                    .addConstraintViolation();
            return false;
        }

        if (value.subjectiveAnswer() == null || value.subjectiveAnswer().isBlank()) {
            context.buildConstraintViolationWithTemplate("subjectiveAnswer must not be blank for SUBJECTIVE answerType")
                    .addPropertyNode("subjectiveAnswer")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
