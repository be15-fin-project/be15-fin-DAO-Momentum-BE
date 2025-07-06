package com.dao.momentum.evaluation.eval.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "다면 평가 제출 요청 DTO")
public record EvalSubmitRequest(

        @Schema(description = "회차 ID", example = "3")
        @NotNull
        Integer roundId,

        @Schema(description = "평가 양식 ID", example = "1")
        Integer formId,

        @Schema(description = "피평가자 ID", example = "1002")
        Long targetId,

        @Schema(description = "작성 사유", example = "스트레스 점수가 낮아짐")
        String reason,

        @Schema(description = "요인별 점수 리스트")
        @Size(min = 1)
        List<EvalFactorScoreDto> factorScores
) {

    public EvalResponseCreateDTO toResponseDto() {
        return EvalResponseCreateDTO.builder()
                .roundId(roundId)
                .formId(formId)
                .targetId(targetId)
                .reason(reason)
                .build();
    }
}
