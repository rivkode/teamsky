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
@Table(name = "problem_answer_keys")
public class ProblemAnswerKeyJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "answer_format", nullable = false, length = 30)
    private ProblemAnswerFormat answerFormat;

    @Column(name = "choice_sequence")
    private Integer choiceSequence;

    @Column(name = "subjective_answer", columnDefinition = "text")
    private String subjectiveAnswer;

    protected ProblemAnswerKeyJpaEntity() {
    }

    public static ProblemAnswerKeyJpaEntity objective(Long problemId, Integer choiceSequence) {
        ProblemAnswerKeyJpaEntity entity = new ProblemAnswerKeyJpaEntity();
        entity.problemId = problemId;
        entity.answerFormat = ProblemAnswerFormat.OBJECTIVE;
        entity.choiceSequence = choiceSequence;
        return entity;
    }

    public static ProblemAnswerKeyJpaEntity subjective(Long problemId, String subjectiveAnswer) {
        ProblemAnswerKeyJpaEntity entity = new ProblemAnswerKeyJpaEntity();
        entity.problemId = problemId;
        entity.answerFormat = ProblemAnswerFormat.SUBJECTIVE;
        entity.subjectiveAnswer = subjectiveAnswer;
        return entity;
    }

    public Long getProblemId() {
        return problemId;
    }

    public Integer getChoiceSequence() {
        return choiceSequence;
    }

    public String getSubjectiveAnswer() {
        return subjectiveAnswer;
    }
}
