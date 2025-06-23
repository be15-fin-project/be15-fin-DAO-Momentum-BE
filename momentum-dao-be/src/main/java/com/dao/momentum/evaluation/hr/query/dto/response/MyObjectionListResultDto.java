package com.dao.momentum.evaluation.hr.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "사원이 조회하는 이의제기 목록 결과 DTO")
public class MyObjectionListResultDto {

    @Schema(description = "목록 데이터")
    private List<MyObjectionItemDto> content;

    @Schema(description = "페이지네이션 정보")
    private Pagination pagination;
}
