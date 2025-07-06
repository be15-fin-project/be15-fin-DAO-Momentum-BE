package com.dao.momentum.evaluation.eval.command.application.dto.response;

import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvalRound;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "다면 평가 회차 등록 응답 DTO")
@Builder
public record EvalRoundCreateResponse(

        @Schema(description = "생성된 회차 ID", example = "12")
        Integer roundId,

        @Schema(description = "성공 메시지", example = "평가 회차가 성공적으로 등록되었습니다.")
        String message
) {

    public static EvalRoundCreateResponse from(EvalRound round) {
        return EvalRoundCreateResponse.builder()
                .roundId(round.getRoundId())
                .message("평가 회차가 성공적으로 등록되었습니다.")
                .build();
    }
}
