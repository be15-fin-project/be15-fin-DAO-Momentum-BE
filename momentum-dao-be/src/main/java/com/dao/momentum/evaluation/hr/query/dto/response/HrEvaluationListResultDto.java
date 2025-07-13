package com.dao.momentum.evaluation.hr.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "사원 인사평가 내역 목록 조회 응답 DTO")
public record HrEvaluationListResultDto(
        @Schema(description = "평가 내역 및 내역별 요인 점수 목록")
        List<HrEvaluationWithFactorsDto> items,

        @Schema(description = "페이지네이션 정보")
        Pagination pagination
) {
}
