package com.dao.momentum.evaluation.eval.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "다면 평가 회차 목록 응답 DTO")
@Builder
public record EvaluationRoundListResultDto(

        @Schema(description = "평가 회차 목록")
        List<EvaluationRoundResponseDto> list,

        @Schema(description = "페이지네이션 정보")
        Pagination pagination
) { }
