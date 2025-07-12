package com.dao.momentum.retention.prospect.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "조직 전체 안정성 유형 분포 요약 DTO")
public record StabilityRatioSummaryDto(

        @Schema(description = "안정형 인원 수", example = "100")
        Long stableCount,

        @Schema(description = "경고형 인원 수", example = "15")
        Long warningCount,

        @Schema(description = "불안정형 인원 수", example = "9")
        Long unstableCount,

        @Schema(description = "전체 인원 수", example = "124")
        Long totalCount
) {}
