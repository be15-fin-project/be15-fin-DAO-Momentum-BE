package com.dao.momentum.evaluation.hr.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사원 인사평가 내역 목록 조회 응답 DTO")
public class HrEvaluationListResultDto {

    @Schema(description = "평가 내역 목록")
    private List<HrEvaluationItemDto> items;

    @Schema(description = "요인별 점수 목록")
    private List<FactorScoreDto> factorScores;

    @Schema(description = "페이지네이션 정보")
    private Pagination pagination;
}
