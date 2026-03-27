package com.project.quiz.api.solving;

import com.project.quiz.domain.problem.ProblemAnswerFormat;
import com.project.quiz.domain.solving.AnswerStatus;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "문제 제출 응답")
public record SubmitProblemAnswerResponse(
        @Schema(description = "문제 ID", example = "1001")
        Long problemId,
        @Schema(description = "답안 형식", example = "OBJECTIVE")
        ProblemAnswerFormat answerType,
        @Schema(description = "채점 결과", example = "PARTIAL")
        AnswerStatus answerStatus,
        @Schema(description = "문제 해설", example = "정답은 boolean 입니다.")
        String explanation,
        @ArraySchema(schema = @Schema(description = "정답 목록", example = "3"))
        List<String> problemAnswers
) {
}
