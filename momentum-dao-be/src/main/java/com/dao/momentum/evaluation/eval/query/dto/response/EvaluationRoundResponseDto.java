package com.dao.momentum.evaluation.eval.query.dto.response;

import com.dao.momentum.evaluation.eval.command.domain.aggregate.EvaluationRoundStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@Schema(description = "다면 평가 회차 응답 DTO")
public class EvaluationRoundResponseDto {

    @Schema(description = "회차 ID", example = "1")
    private final Integer roundId;

    @Schema(description = "회차 번호", example = "2")
    private final Integer roundNo;

    @Schema(description = "평가 시작일", example = "2025-07-01", type = "string", format = "date")
    private final LocalDate startAt;

    @Schema(description = "평가 종료일", example = "2025-07-07", type = "string", format = "date")
    private final LocalDate endAt;

    @Schema(description = "참여자 수", example = "20")
    private final Integer participantCount;

    @Schema(
            description = "평가 상태",
            example = "IN_PROGRESS",
            allowableValues = {"BEFORE", "IN_PROGRESS", "DONE"}
    )
    private final EvaluationRoundStatus status;
}
