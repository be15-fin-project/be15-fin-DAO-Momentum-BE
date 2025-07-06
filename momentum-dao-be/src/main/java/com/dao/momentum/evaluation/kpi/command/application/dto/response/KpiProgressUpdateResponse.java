package com.dao.momentum.evaluation.kpi.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "KPI 진척도 최신화 응답")
public record KpiProgressUpdateResponse(

        @Schema(description = "KPI ID", example = "101")
        Long kpiId,

        @Schema(description = "진척도", example = "75")
        Integer progress,

        @Schema(description = "처리 결과 메시지", example = "KPI 진척도가 성공적으로 업데이트되었습니다.")
        String message

) {}
