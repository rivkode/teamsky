package com.project.quiz.domain.problem;

import com.project.quiz.domain.solving.AnswerStatus;
import com.project.quiz.domain.solving.GradingResult;
import com.project.quiz.domain.solving.SubmittedAnswer;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public record Problem(
        Long id,
        Long chapterId,
        String content,
        ProblemAnswerFormat answerFormat,
        ProblemType type,
        List<ProblemChoice> choices,
        ProblemAnswerKey answerKey,
        String explanation
) {

    public boolean belongsTo(Long targetChapterId) {
        return chapterId.equals(targetChapterId);
    }

    public GradingResult grade(SubmittedAnswer submittedAnswer) {
        return switch (answerFormat) {
            case OBJECTIVE -> gradeObjective(submittedAnswer);
            case SUBJECTIVE -> gradeSubjective(submittedAnswer);
        };
    }

    private GradingResult gradeObjective(SubmittedAnswer submittedAnswer) {
        Set<Integer> correctAnswers = answerKey.objectiveAnswers();
        Set<Integer> submittedChoices = submittedAnswer.selectedChoices() == null
                ? Set.of()
                : Set.copyOf(submittedAnswer.selectedChoices());

        AnswerStatus answerStatus;
        if (submittedChoices.equals(correctAnswers)) {
            answerStatus = AnswerStatus.CORRECT;
        } else if (submittedChoices.stream().anyMatch(correctAnswers::contains)) {
            answerStatus = AnswerStatus.PARTIAL;
        } else {
            answerStatus = AnswerStatus.INCORRECT;
        }

        return new GradingResult(
                id,
                answerFormat,
                answerStatus,
                explanation,
                correctAnswers.stream().sorted().map(String::valueOf).toList()
        );
    }

    private GradingResult gradeSubjective(SubmittedAnswer submittedAnswer) {
        String normalizedSubmittedAnswer = normalize(submittedAnswer.subjectiveAnswer());
        boolean correct = answerKey.subjectiveAnswers().stream()
                .map(this::normalize)
                .anyMatch(normalizedSubmittedAnswer::equals);

        return new GradingResult(
                id,
                answerFormat,
                correct ? AnswerStatus.CORRECT : AnswerStatus.INCORRECT,
                explanation,
                answerKey.subjectiveAnswers()
        );
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
