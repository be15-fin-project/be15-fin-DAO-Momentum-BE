package com.dao.momentum.evaluation.hr.command.application.dto.request;

import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalScore;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.List;

@Schema(description = "이의제기 처리 요청 DTO (승인 or 반려)")
@Builder
public record HrObjectionProcessRequestDto(

        @Schema(description = "처리할 이의 제기 ID", example = "12")
        @NotNull
        Long objectionId,

        @Schema(description = "승인 여부: true면 승인, false면 반려", example = "true")
        @NotNull
        Boolean approved,

        @Schema(description = "반려 사유 (승인 아닌 경우 필수)", example = "근거가 부족함")
        String rejectReason,

        @Schema(description = "요인별 점수 목록 (승인 시 필수)")
        List<EvalFactorScoreDto> factorScores
) {

    @Schema(description = "요인별 점수 입력 DTO")
    public static record EvalFactorScoreDto(

            @Schema(description = "평가 요인 ID", example = "1")
            @NotNull
            Integer propertyId,

            @Schema(description = "점수 (0~100)", example = "80")
            @Min(0)
            @Max(100)
            Integer score
    ) {

        public static EvalScore toEntity(Long resultId, Integer propertyId, Integer score) {
            return EvalScore.builder()
                    .resultId(resultId)
                    .propertyId(propertyId)
                    .score(score)
                    .build();
        }
    }
}
