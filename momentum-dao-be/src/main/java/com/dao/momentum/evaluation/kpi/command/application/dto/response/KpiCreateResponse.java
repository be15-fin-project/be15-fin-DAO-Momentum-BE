package com.dao.momentum.evaluation.kpi.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "KPI 생성 응답")
public record KpiCreateResponse(

        @Schema(description = "생성된 KPI ID", example = "101")
        Long kpiId,

        @Schema(description = "처리 결과 메시지", example = "KPI가 성공적으로 생성되었습니다.")
        String message

) {}
