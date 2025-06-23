package com.dao.momentum.evaluation.eval.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "사원 간 평가 목록 응답 DTO")
public class PeerEvaluationListResultDto {

    @Schema(description = "사원 간 평가 목록")
    private List<PeerEvaluationResponseDto> list;

    @Schema(description = "페이지네이션 정보")
    private Pagination pagination;
}
