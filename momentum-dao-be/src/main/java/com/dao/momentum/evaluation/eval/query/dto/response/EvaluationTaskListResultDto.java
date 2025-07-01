package com.dao.momentum.evaluation.eval.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "평가 태스크 목록 결과 DTO")
public class EvaluationTaskListResultDto {
    @Schema(description = "평가 태스크 목록")
    private java.util.List<EvaluationTaskResponseDto> tasks;

    @Schema(description = "페이징 정보")
    private Pagination pagination;
}
