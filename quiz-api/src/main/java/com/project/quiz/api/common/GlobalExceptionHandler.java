package com.project.quiz.api.common;

import com.project.quiz.application.solving.exception.ChapterNotFoundException;
import com.project.quiz.application.solving.exception.NoAvailableProblemException;
import com.project.quiz.application.solving.exception.ProblemAnswerTypeMismatchException;
import com.project.quiz.application.solving.exception.ProblemNotFoundInChapterException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ChapterNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleChapterNotFound(ChapterNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("CHAPTER_NOT_FOUND", exception.getMessage()));
    }

    @ExceptionHandler(NoAvailableProblemException.class)
    public ResponseEntity<ErrorResponse> handleNoAvailableProblem(NoAvailableProblemException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("NO_AVAILABLE_PROBLEM", exception.getMessage()));
    }

    @ExceptionHandler(ProblemNotFoundInChapterException.class)
    public ResponseEntity<ErrorResponse> handleProblemNotFoundInChapter(ProblemNotFoundInChapterException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("PROBLEM_NOT_FOUND_IN_CHAPTER", exception.getMessage()));
    }

    @ExceptionHandler(ProblemAnswerTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleProblemAnswerTypeMismatch(ProblemAnswerTypeMismatchException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("PROBLEM_ANSWER_TYPE_MISMATCH", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest()
                .body(new ErrorResponse("INVALID_REQUEST", message));
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}
