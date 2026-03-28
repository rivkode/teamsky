package com.project.quiz.api.problem;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "랜덤 문제 조회 응답")
public record GetRandomProblemResponse(
        @Schema(description = "문제 ID", example = "1")
        Long problemId,

        @Schema(description = "문제 내용", example = "문제 설명")
        String content,

        @ArraySchema(schema = @Schema(description = "객관식 선택지 목록", example = "지문1"))
        List<String> choices,

        @Schema(description = "문제 정답률. 30명 미만이 풀었으면 null 입니다.", example = "67", nullable = true)
        Integer answerCorrectRate
) {
}
