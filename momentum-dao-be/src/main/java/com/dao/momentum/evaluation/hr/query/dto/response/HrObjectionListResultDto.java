package com.dao.momentum.evaluation.hr.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "인사 평가 이의 제기 목록 응답 DTO")
public class HrObjectionListResultDto {

    @Schema(description = "인사 평가 이의 제기 목록 데이터")
    private List<HrObjectionItemDto> list;

    @Schema(description = "페이지네이션 정보")
    private Pagination pagination;
}
