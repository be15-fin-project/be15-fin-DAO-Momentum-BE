package com.dao.momentum.retention.prospect.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "근속 전망 인사이트 저장용 DTO")
public record RetentionInsightDto(

        @Schema(description = "부서 ID", example = "101")
        Integer deptId,

        @Schema(description = "해당 부서 평균 근속 전망 점수", example = "76")
        int retentionScore,

        @Schema(description = "소속 인원 수", example = "25")
        int empCount,

        @Schema(description = "진척도 20% 이하 인원 수", example = "3")
        int progress20,

        @Schema(description = "진척도 40% 이하 인원 수", example = "5")
        int progress40,

        @Schema(description = "진척도 60% 이하 인원 수", example = "7")
        int progress60,

        @Schema(description = "진척도 80% 이하 인원 수", example = "6")
        int progress80,

        @Schema(description = "진척도 100% 인원 수", example = "4")
        int progress100
) {}
