package com.dao.momentum.evaluation.eval.query.dto.response;

import com.dao.momentum.evaluation.eval.query.dto.request.NoneSubmitDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "미제출자 목록 응답 DTO")
public record NoneSubmitListResultDto(
        @Schema(description = "미제출자 목록")
        List<NoneSubmitDto> items
) {}
