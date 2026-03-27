package com.project.quiz.infrastructure.persistence.entity;

import com.project.quiz.domain.problem.ProblemType;
import com.project.quiz.domain.problem.ProblemAnswerFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "problems")
public class ProblemJpaEntity {

    @Id
    private Long id;

    @Column(name = "chapter_id", nullable = false)
    private Long chapterId;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "answer_format", nullable = false, length = 30)
    private ProblemAnswerFormat answerFormat;

    @Enumerated(EnumType.STRING)
    @Column(name = "problem_type", nullable = false, length = 30)
    private ProblemType type;

    @Column(nullable = false, columnDefinition = "text")
    private String explanation;

    protected ProblemJpaEntity() {
    }

    public static ProblemJpaEntity create(
            Long id,
            Long chapterId,
            String content,
            ProblemAnswerFormat answerFormat,
            ProblemType type,
            String explanation
    ) {
        ProblemJpaEntity entity = new ProblemJpaEntity();
        entity.id = id;
        entity.chapterId = chapterId;
        entity.content = content;
        entity.answerFormat = answerFormat;
        entity.type = type;
        entity.explanation = explanation;
        return entity;
    }

    public Long getId() {
        return id;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public String getContent() {
        return content;
    }

    public ProblemAnswerFormat getAnswerFormat() {
        return answerFormat;
    }

    public ProblemType getType() {
        return type;
    }

    public String getExplanation() {
        return explanation;
    }
}
