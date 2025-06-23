package com.dao.momentum.evaluation.eval.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "조직 평가 내역 목록 응답 DTO")
public class OrgEvaluationListResultDto {

    @Schema(description = "조직 평가 응답 리스트")
    private List<OrgEvaluationResponseDto> list;

    @Schema(description = "페이지네이션 정보")
    private Pagination pagination;
}
