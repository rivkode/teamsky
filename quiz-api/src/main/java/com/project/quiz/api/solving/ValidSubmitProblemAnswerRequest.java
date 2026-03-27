package com.project.quiz.api.solving;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SubmitProblemAnswerRequestValidator.class)
public @interface ValidSubmitProblemAnswerRequest {

    String message() default "invalid submit problem answer request";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
