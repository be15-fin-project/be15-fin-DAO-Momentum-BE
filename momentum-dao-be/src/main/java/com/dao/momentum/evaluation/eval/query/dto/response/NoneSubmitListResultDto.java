package com.dao.momentum.evaluation.eval.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "미제출자 목록 응답 DTO")
public class NoneSubmitListResultDto {

    @Schema(description = "미제출자 목록")
    private List<NoneSubmitDto> items;
}
