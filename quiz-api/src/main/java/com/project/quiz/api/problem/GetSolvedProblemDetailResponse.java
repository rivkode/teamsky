package com.project.quiz.api.problem;

import com.project.quiz.domain.problem.ProblemAnswerFormat;
import com.project.quiz.domain.solving.AnswerStatus;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "풀었던 문제 상세 조회 응답")
public record GetSolvedProblemDetailResponse(
        @Schema(description = "문제 ID", example = "3")
        Long problemId,
        @Schema(description = "답안 형식", example = "OBJECTIVE")
        ProblemAnswerFormat answerType,
        @Schema(description = "채점 결과", example = "CORRECT")
        AnswerStatus answerStatus,
        @Schema(description = "문제 해설", example = "정답은 1번과 2번입니다.")
        String explanation,
        @ArraySchema(schema = @Schema(description = "문제 정답", example = "1"))
        List<String> problemAnswers,
        @ArraySchema(schema = @Schema(description = "사용자 제출 답안", example = "3"))
        List<String> userAnswers,
        @Schema(description = "문제 정답률", example = "67")
        Integer answerCorrectRate
) {
}
