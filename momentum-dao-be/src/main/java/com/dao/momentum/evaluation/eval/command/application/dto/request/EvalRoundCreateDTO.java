package com.dao.momentum.evaluation.eval.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Schema(description = "다면 평가 회차 생성 요청 DTO")
@Builder
public record EvalRoundCreateDTO(

        @Schema(description = "회차 번호", example = "3")
        int roundNo,

        @Schema(description = "평가 시작일", example = "2025-07-01")
        LocalDate startAt
) { }
