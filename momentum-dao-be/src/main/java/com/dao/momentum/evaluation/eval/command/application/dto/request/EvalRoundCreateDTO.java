package com.dao.momentum.evaluation.eval.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@Schema(description = "다면 평가 회차 생성 요청 DTO")
public class EvalRoundCreateDTO {

    @Schema(description = "회차 번호", example = "3")
    private final int roundNo;

    @Schema(description = "평가 시작일", example = "2025-07-01")
    private final LocalDate startAt;
}
