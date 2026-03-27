package com.project.quiz.api.solving;

import com.project.quiz.application.solving.port.in.SubmitProblemAnswerCommand;
import com.project.quiz.application.solving.port.in.SubmitProblemAnswerResult;
import com.project.quiz.application.solving.port.in.SubmitProblemAnswerUseCase;
import com.project.quiz.api.common.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "단원별 문제 풀이", description = "단원별 랜덤 문제 조회와 풀이 관련 엔드포인트")
@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class SubmitProblemAnswerController {

    private final SubmitProblemAnswerUseCase submitProblemAnswerUseCase;

    @Operation(summary = "문제 제출", description = "객관식 또는 주관식 답안을 제출하고 즉시 채점 결과와 해설을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "문제 제출이 정상적으로 처리되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "문제를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "문제의 답안 형식과 제출 형식이 일치하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/submit")
    public ResponseEntity<SubmitProblemAnswerResponse> submit(@Valid @RequestBody SubmitProblemAnswerRequest request) {
        SubmitProblemAnswerResult result = submitProblemAnswerUseCase.submit(
                new SubmitProblemAnswerCommand(
                        request.problemId(),
                        request.userId(),
                        request.answerType(),
                        request.selectedChoices(),
                        request.subjectiveAnswer()
                )
        );

        return ResponseEntity.ok(new SubmitProblemAnswerResponse(
                result.problemId(),
                result.answerType(),
                result.answerStatus(),
                result.explanation(),
                result.problemAnswers()
        ));
    }
}
