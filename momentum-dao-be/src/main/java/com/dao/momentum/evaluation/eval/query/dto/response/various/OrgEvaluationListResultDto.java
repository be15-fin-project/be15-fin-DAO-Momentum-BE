package com.dao.momentum.evaluation.eval.query.dto.response.various;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "조직 평가 내역 목록 응답 DTO")
@Builder
public record OrgEvaluationListResultDto(

        @Schema(description = "조직 평가 응답 리스트")
        List<OrgEvaluationResponseDto> list,

        @Schema(description = "페이지네이션 정보")
        Pagination pagination
) { }
