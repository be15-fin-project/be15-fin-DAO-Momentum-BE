package com.dao.momentum.evaluation.eval.query.dto.response;

import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvaluationRoundStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Schema(description = "다면 평가 회차 응답 DTO")
@Builder
public record EvaluationRoundResponseDto(

        @Schema(description = "회차 ID", example = "1")
        Integer roundId,

        @Schema(description = "회차 번호", example = "2")
        Integer roundNo,

        @Schema(description = "평가 시작일", example = "2025-07-01", type = "string", format = "date")
        LocalDate startAt,

        @Schema(description = "평가 종료일", example = "2025-07-07", type = "string", format = "date")
        LocalDate endAt,

        @Schema(description = "참여자 수", example = "20")
        Integer participantCount,

        @Schema(
                description = "평가 상태",
                example = "IN_PROGRESS",
                allowableValues = {"BEFORE", "IN_PROGRESS", "DONE"}
        )
        EvaluationRoundStatus status
) { }
