package com.dao.momentum.evaluation.eval.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "평가 태스크 목록 결과 DTO")
@Builder
public record EvaluationTaskListResultDto(

        @Schema(description = "평가 태스크 목록")
        List<EvaluationTaskResponseDto> tasks,

        @Schema(description = "페이징 정보")
        Pagination pagination
) { }
