package com.dao.momentum.evaluation.eval.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "다면 평가 회차 목록 응답 DTO")
public class EvaluationRoundListResultDto {

    @Schema(description = "평가 회차 목록")
    private List<EvaluationRoundResponseDto> list;

    @Schema(description = "페이지네이션 정보")
    private Pagination pagination;
}
