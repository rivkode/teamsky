package com.project.quiz.api.common;

import com.project.quiz.application.solving.exception.ChapterNotFoundException;
import com.project.quiz.application.solving.exception.InvalidProblemChoiceException;
import com.project.quiz.application.solving.exception.NoAvailableProblemException;
import com.project.quiz.application.solving.exception.ProblemAnswerTypeMismatchException;
import com.project.quiz.application.solving.exception.ProblemNotFoundInChapterException;
import com.project.quiz.application.solving.exception.SolvedProblemNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ChapterNotFoundException.class)
    public ResponseEntity<CommonResponse<Void>> handleChapterNotFound(ChapterNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.failure("CHAPTER_NOT_FOUND", exception.getMessage()));
    }

    @ExceptionHandler(NoAvailableProblemException.class)
    public ResponseEntity<CommonResponse<Void>> handleNoAvailableProblem(NoAvailableProblemException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(CommonResponse.failure("NO_AVAILABLE_PROBLEM", exception.getMessage()));
    }

    @ExceptionHandler(ProblemNotFoundInChapterException.class)
    public ResponseEntity<CommonResponse<Void>> handleProblemNotFoundInChapter(ProblemNotFoundInChapterException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.failure("PROBLEM_NOT_FOUND_IN_CHAPTER", exception.getMessage()));
    }

    @ExceptionHandler(SolvedProblemNotFoundException.class)
    public ResponseEntity<CommonResponse<Void>> handleSolvedProblemNotFound(SolvedProblemNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.failure("SOLVED_PROBLEM_NOT_FOUND", exception.getMessage()));
    }

    @ExceptionHandler(ProblemAnswerTypeMismatchException.class)
    public ResponseEntity<CommonResponse<Void>> handleProblemAnswerTypeMismatch(ProblemAnswerTypeMismatchException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(CommonResponse.failure("PROBLEM_ANSWER_TYPE_MISMATCH", exception.getMessage()));
    }

    @ExceptionHandler(InvalidProblemChoiceException.class)
    public ResponseEntity<CommonResponse<Void>> handleInvalidProblemChoice(InvalidProblemChoiceException exception) {
        return ResponseEntity.badRequest()
                .body(CommonResponse.failure("INVALID_PROBLEM_CHOICE", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest()
                .body(CommonResponse.failure("INVALID_REQUEST", message));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<CommonResponse<Void>> handleBindException(BindException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest()
                .body(CommonResponse.failure("INVALID_REQUEST", message));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonResponse<Void>> handleRuntimeException(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.failure("INTERNAL_SERVER_ERROR", "Unexpected server error"));
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}
