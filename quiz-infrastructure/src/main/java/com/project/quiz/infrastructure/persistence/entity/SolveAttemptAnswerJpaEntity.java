package com.project.quiz.infrastructure.persistence.entity;

import com.project.quiz.domain.problem.ProblemAnswerFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "solve_attempt_answers")
public class SolveAttemptAnswerJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "solve_attempt_id", nullable = false)
    private Long solveAttemptId;

    @Enumerated(EnumType.STRING)
    @Column(name = "answer_format", nullable = false, length = 30)
    private ProblemAnswerFormat answerFormat;

    @Column(name = "choice_sequence")
    private Integer choiceSequence;

    @Column(name = "subjective_answer", columnDefinition = "text")
    private String subjectiveAnswer;

    protected SolveAttemptAnswerJpaEntity() {
    }

    public static SolveAttemptAnswerJpaEntity objective(Long solveAttemptId, Integer choiceSequence) {
        SolveAttemptAnswerJpaEntity entity = new SolveAttemptAnswerJpaEntity();
        entity.solveAttemptId = solveAttemptId;
        entity.answerFormat = ProblemAnswerFormat.OBJECTIVE;
        entity.choiceSequence = choiceSequence;
        return entity;
    }

    public static SolveAttemptAnswerJpaEntity subjective(Long solveAttemptId, String subjectiveAnswer) {
        SolveAttemptAnswerJpaEntity entity = new SolveAttemptAnswerJpaEntity();
        entity.solveAttemptId = solveAttemptId;
        entity.answerFormat = ProblemAnswerFormat.SUBJECTIVE;
        entity.subjectiveAnswer = subjectiveAnswer;
        return entity;
    }

    public Long getSolveAttemptId() {
        return solveAttemptId;
    }

    public ProblemAnswerFormat getAnswerFormat() {
        return answerFormat;
    }

    public Integer getChoiceSequence() {
        return choiceSequence;
    }

    public String getSubjectiveAnswer() {
        return subjectiveAnswer;
    }
}
