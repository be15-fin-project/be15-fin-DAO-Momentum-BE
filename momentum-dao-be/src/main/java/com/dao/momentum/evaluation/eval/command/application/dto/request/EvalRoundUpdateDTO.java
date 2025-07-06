package com.dao.momentum.evaluation.eval.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Schema(description = "다면 평가 회차 수정 요청 DTO")
@Builder
public record EvalRoundUpdateDTO(

        @Schema(description = "수정 대상 회차 ID", example = "1")
        Integer roundId,

        @Schema(description = "수정할 회차 번호", example = "4")
        int roundNo,

        @Schema(description = "수정할 평가 시작일", example = "2025-07-15")
        LocalDate startAt
) { }
