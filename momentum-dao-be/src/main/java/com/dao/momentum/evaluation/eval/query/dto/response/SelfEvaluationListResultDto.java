package com.dao.momentum.evaluation.eval.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "자가 진단 목록 응답 DTO")
public class SelfEvaluationListResultDto {

    @Schema(description = "자가 진단 목록")
    private List<SelfEvaluationResponseDto> list;

    @Schema(description = "페이지네이션 정보")
    private Pagination pagination;
}
