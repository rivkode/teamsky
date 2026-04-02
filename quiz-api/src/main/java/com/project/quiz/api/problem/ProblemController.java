package com.project.quiz.api.problem;

import com.project.quiz.api.common.CommonErrorResponse;
import com.project.quiz.api.common.CommonResponse;
import com.project.quiz.application.solving.model.GetRandomProblemCommand;
import com.project.quiz.application.solving.model.GetRandomProblemResult;
import com.project.quiz.application.solving.model.GetSolvedProblemDetailCommand;
import com.project.quiz.application.solving.model.GetSolvedProblemDetailResult;
import com.project.quiz.application.solving.model.SkipProblemCommand;
import com.project.quiz.application.solving.model.SubmitProblemAnswerCommand;
import com.project.quiz.application.solving.model.SubmitProblemAnswerResult;
import com.project.quiz.application.solving.service.GetRandomProblemService;
import com.project.quiz.application.solving.service.GetSolvedProblemDetailService;
import com.project.quiz.application.solving.service.SkipProblemService;
import com.project.quiz.application.solving.service.SubmitProblemAnswerService;
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
public class ProblemController {

    private final GetRandomProblemService getRandomProblemService;
    private final GetSolvedProblemDetailService getSolvedProblemDetailService;
    private final SkipProblemService skipProblemService;
    private final SubmitProblemAnswerService submitProblemAnswerService;

    @Operation(
            summary = "랜덤 미풀이 문제 조회",
            description = "선택한 단원에서 이미 푼 문제와 직전에 건너뛴 문제를 제외하고 랜덤 문제 1개를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "랜덤 문제가 정상적으로 반환되었습니다.",
                    content = @Content(schema = @Schema(implementation = GetRandomProblemCommonResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = CommonErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 단원을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = CommonErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "해당 단원에 더 이상 출제 가능한 문제가 없습니다.",
                    content = @Content(schema = @Schema(implementation = CommonErrorResponse.class))
            )
    })
    @PostMapping("/random")
    public ResponseEntity<CommonResponse<GetRandomProblemResponse>> getRandomProblem(@Valid @RequestBody GetRandomProblemRequest request) {
        GetRandomProblemResult result = getRandomProblemService.getRandomProblem(
                new GetRandomProblemCommand(request.userId(), request.chapterId())
        );
        return ResponseEntity.ok(CommonResponse.success(toRandomProblemResponse(result)));
    }

    @Operation(
            summary = "문제 넘기기",
            description = "현재 문제를 건너뛴 뒤 같은 단원에서 다음 랜덤 문제 1개를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "문제를 건너뛰고 다음 문제가 정상적으로 반환되었습니다.",
                    content = @Content(schema = @Schema(implementation = GetRandomProblemCommonResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = CommonErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 단원 또는 문제가 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = CommonErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "건너뛴 이후 더 이상 출제 가능한 문제가 없습니다.",
                    content = @Content(schema = @Schema(implementation = CommonErrorResponse.class))
            )
    })
    @PostMapping("/skip")
    public ResponseEntity<CommonResponse<GetRandomProblemResponse>> skipProblem(@Valid @RequestBody SkipProblemRequest request) {
        GetRandomProblemResult result = skipProblemService.skipProblem(
                new SkipProblemCommand(request.userId(), request.chapterId(), request.problemId())
        );
        return ResponseEntity.ok(CommonResponse.success(toRandomProblemResponse(result)));
    }

    @Operation(summary = "풀었던 문제 상세 조회", description = "사용자가 이전에 풀었던 문제의 채점 결과, 해설, 정답, 제출 답안을 상세 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "풀었던 문제 상세가 정상적으로 반환되었습니다.",
                    content = @Content(schema = @Schema(implementation = GetSolvedProblemDetailCommonResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = CommonErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "문제 또는 풀이 이력을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = CommonErrorResponse.class))
            )
    })
    @PostMapping("/detail")
    public ResponseEntity<CommonResponse<GetSolvedProblemDetailResponse>> getSolvedProblemDetail(@Valid @RequestBody GetSolvedProblemDetailRequest request) {
        GetSolvedProblemDetailResult result = getSolvedProblemDetailService.getDetail(
                new GetSolvedProblemDetailCommand(request.userId(), request.problemId())
        );

        return ResponseEntity.ok(CommonResponse.success(new GetSolvedProblemDetailResponse(
                result.problemId(),
                result.answerType(),
                result.answerStatus(),
                result.explanation(),
                result.problemAnswers(),
                result.userAnswers(),
                result.answerCorrectRate()
        )));
    }

    @Operation(summary = "문제 제출", description = "객관식 또는 주관식 답안을 제출하고 즉시 채점 결과와 해설을 반환합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "문제 제출이 정상적으로 처리되었습니다.",
                    content = @Content(schema = @Schema(implementation = SubmitProblemAnswerCommonResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = CommonErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "문제를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = CommonErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "문제의 답안 형식과 제출 형식이 일치하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = CommonErrorResponse.class))
            )
    })
    @PostMapping("/submit")
    public ResponseEntity<CommonResponse<SubmitProblemAnswerResponse>> submit(@Valid @RequestBody SubmitProblemAnswerRequest request) {
        SubmitProblemAnswerResult result = submitProblemAnswerService.submit(
                new SubmitProblemAnswerCommand(
                        request.problemId(),
                        request.userId(),
                        request.answerType(),
                        request.selectedChoices(),
                        request.subjectiveAnswer()
                )
        );

        return ResponseEntity.ok(CommonResponse.success(new SubmitProblemAnswerResponse(
                result.problemId(),
                result.answerType(),
                result.answerStatus(),
                result.explanation(),
                result.problemAnswers()
        )));
    }

    private GetRandomProblemResponse toRandomProblemResponse(GetRandomProblemResult result) {
        return new GetRandomProblemResponse(
                result.problemId(),
                result.content(),
                result.choices().stream().map(choice -> choice.content()).toList(),
                result.answerCorrectRate()
        );
    }
}
