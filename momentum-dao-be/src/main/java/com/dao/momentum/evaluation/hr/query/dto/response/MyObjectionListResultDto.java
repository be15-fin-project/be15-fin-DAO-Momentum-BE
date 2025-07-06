package com.dao.momentum.evaluation.hr.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "사원이 조회하는 이의제기 목록 결과 DTO")
public record MyObjectionListResultDto(
        @Schema(description = "목록 데이터")
        List<MyObjectionItemDto> content,

        @Schema(description = "페이지네이션 정보")
        Pagination pagination
) {
}
