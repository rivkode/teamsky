package com.project.quiz.api.solving;

import com.project.quiz.domain.problem.ProblemAnswerFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

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
            return true;
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
